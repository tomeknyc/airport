/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.internal.artifacts.repositories.cachemanager;

import org.apache.ivy.core.cache.ArtifactOrigin;
import org.apache.ivy.core.cache.CacheDownloadOptions;
import org.apache.ivy.core.cache.CacheMetadataOptions;
import org.apache.ivy.core.cache.DownloadListener;
import org.apache.ivy.core.module.descriptor.Artifact;
import org.apache.ivy.core.module.descriptor.DependencyDescriptor;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.module.id.ArtifactRevisionId;
import org.apache.ivy.core.report.ArtifactDownloadReport;
import org.apache.ivy.core.report.DownloadStatus;
import org.apache.ivy.core.report.MetadataArtifactDownloadReport;
import org.apache.ivy.core.resolve.ResolvedModuleRevision;
import org.apache.ivy.plugins.repository.ArtifactResourceResolver;
import org.apache.ivy.plugins.repository.ResourceDownloader;
import org.apache.ivy.plugins.resolver.DependencyResolver;
import org.apache.ivy.plugins.resolver.util.ResolvedResource;
import org.apache.ivy.util.Message;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.ArtifactOriginWithMetaData;
import org.gradle.api.internal.artifacts.repositories.EnhancedArtifactDownloadReport;
import org.gradle.api.internal.externalresource.ExternalResource;
import org.gradle.api.internal.externalresource.cached.CachedExternalResourceIndex;
import org.gradle.api.internal.externalresource.metadata.ExternalResourceMetaData;
import org.gradle.api.internal.filestore.FileStore;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/**
 * A cache manager for remote repositories, that downloads files and stores them in the FileStore provided.
 */
public class DownloadingRepositoryCacheManager extends AbstractRepositoryCacheManager {
    private final FileStore<ArtifactRevisionId> fileStore;
    private final CachedExternalResourceIndex<String> artifactUrlCachedResolutionIndex;

    public DownloadingRepositoryCacheManager(String name, FileStore<ArtifactRevisionId> fileStore, CachedExternalResourceIndex<String> artifactUrlCachedResolutionIndex) {
        super(name);
        this.fileStore = fileStore;
        this.artifactUrlCachedResolutionIndex = artifactUrlCachedResolutionIndex;
    }

    public ArtifactDownloadReport download(Artifact artifact, ArtifactResourceResolver resourceResolver,
                                           ResourceDownloader resourceDownloader, CacheDownloadOptions options) {
        EnhancedArtifactDownloadReport adr = new EnhancedArtifactDownloadReport(artifact);

        DownloadListener listener = options.getListener();
        if (listener != null) {
            listener.needArtifact(this, artifact);
        }

        long start = System.currentTimeMillis();
        try {
            ResolvedResource artifactRef = resourceResolver.resolve(artifact);
            if (artifactRef != null) {
                ArtifactOrigin origin = new ArtifactOriginWithMetaData(artifact, artifactRef.getResource());
                if (listener != null) {
                    listener.startArtifactDownload(this, artifactRef, artifact, origin);
                }

                File artifactFile = downloadArtifactFile(artifact, resourceDownloader, artifactRef);

                adr.setDownloadTimeMillis(System.currentTimeMillis() - start);
                adr.setSize(artifactFile.length());
                adr.setDownloadStatus(DownloadStatus.SUCCESSFUL);
                adr.setArtifactOrigin(origin);
                adr.setLocalFile(artifactFile);
            } else {
                adr.setDownloadTimeMillis(System.currentTimeMillis() - start);
                adr.setDownloadStatus(DownloadStatus.FAILED);
                adr.setDownloadDetails(ArtifactDownloadReport.MISSING_ARTIFACT);
            }
        } catch (Throwable throwable) {
            adr.setDownloadTimeMillis(System.currentTimeMillis() - start);
            adr.failed(throwable);
        }
        if (listener != null) {
            listener.endArtifactDownload(this, artifact, adr, adr.getLocalFile());
        }
        return adr;
    }

    private File downloadArtifactFile(Artifact artifact, ResourceDownloader resourceDownloader, ResolvedResource artifactRef) throws IOException {
        File tempFile = fileStore.getTempFile();
        resourceDownloader.download(artifact, artifactRef.getResource(), tempFile);

        File fileInFileStore = fileStore.move(artifact.getId(), tempFile).getFile();

        if (artifactRef.getResource() instanceof ExternalResource) {
            ExternalResource resource = (ExternalResource) artifactRef.getResource();
            ExternalResourceMetaData metaData = resource.getMetaData();
            artifactUrlCachedResolutionIndex.store(metaData.getLocation(), fileInFileStore, metaData);
        }

        return fileInFileStore;
    }

    public ResolvedModuleRevision cacheModuleDescriptor(DependencyResolver resolver, final ResolvedResource resolvedResource, DependencyDescriptor dd, Artifact moduleArtifact, ResourceDownloader downloader, CacheMetadataOptions options) throws ParseException {
        if (!moduleArtifact.isMetadata()) {
            return null;
        }

        ArtifactResourceResolver artifactResourceResolver = new ArtifactResourceResolver() {
            public ResolvedResource resolve(Artifact artifact) {
                return resolvedResource;
            }
        };
        ArtifactDownloadReport report = download(moduleArtifact, artifactResourceResolver, downloader, new CacheDownloadOptions().setListener(options.getListener()).setForce(true));

        if (report.getDownloadStatus() == DownloadStatus.FAILED) {
            Message.warn("problem while downloading module descriptor: " + resolvedResource.getResource()
                    + ": " + report.getDownloadDetails()
                    + " (" + report.getDownloadTimeMillis() + "ms)");
            return null;
        }

        ModuleDescriptor md = parseModuleDescriptor(resolver, moduleArtifact, options, report.getLocalFile(), resolvedResource.getResource());
        Message.debug("\t" + getName() + ": parsed downloaded md file for " + moduleArtifact.getModuleRevisionId() + "; parsed=" + md.getModuleRevisionId());

        MetadataArtifactDownloadReport madr = new MetadataArtifactDownloadReport(md.getMetadataArtifact());
        madr.setSearched(true);
        madr.setDownloadStatus(report.getDownloadStatus());
        madr.setDownloadDetails(report.getDownloadDetails());
        madr.setArtifactOrigin(report.getArtifactOrigin());
        madr.setDownloadTimeMillis(report.getDownloadTimeMillis());
        madr.setOriginalLocalFile(report.getLocalFile());
        madr.setSize(report.getSize());

        return new ResolvedModuleRevision(resolver, resolver, md, madr);
    }

}
