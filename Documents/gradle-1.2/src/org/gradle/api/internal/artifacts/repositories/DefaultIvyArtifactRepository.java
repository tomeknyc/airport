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
package org.gradle.api.internal.artifacts.repositories;

import groovy.lang.Closure;
import org.apache.ivy.core.module.id.ArtifactRevisionId;
import org.apache.ivy.plugins.resolver.DependencyResolver;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.artifacts.repositories.IvyArtifactRepository;
import org.gradle.api.artifacts.repositories.PasswordCredentials;
import org.gradle.api.internal.artifacts.repositories.layout.*;
import org.gradle.api.internal.artifacts.repositories.transport.RepositoryTransportFactory;
import org.gradle.api.internal.externalresource.cached.CachedExternalResourceIndex;
import org.gradle.api.internal.externalresource.local.LocallyAvailableResourceFinder;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.util.ConfigureUtil;
import org.gradle.util.WrapUtil;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;

public class DefaultIvyArtifactRepository extends AbstractAuthenticationSupportedRepository implements IvyArtifactRepository, ArtifactRepositoryInternal {
    private String name;
    private Object baseUrl;
    private RepositoryLayout layout;
    private final AdditionalPatternsRepositoryLayout additionalPatternsLayout;
    private final FileResolver fileResolver;
    private final RepositoryTransportFactory transportFactory;
    private final LocallyAvailableResourceFinder<ArtifactRevisionId> locallyAvailableResourceFinder;
    private final CachedExternalResourceIndex<String> cachedExternalResourceIndex;

    public DefaultIvyArtifactRepository(FileResolver fileResolver, PasswordCredentials credentials, RepositoryTransportFactory transportFactory,
                                        LocallyAvailableResourceFinder<ArtifactRevisionId> locallyAvailableResourceFinder,
                                        CachedExternalResourceIndex<String> cachedExternalResourceIndex) {
        super(credentials);
        this.fileResolver = fileResolver;
        this.transportFactory = transportFactory;
        this.locallyAvailableResourceFinder = locallyAvailableResourceFinder;
        this.cachedExternalResourceIndex = cachedExternalResourceIndex;
        this.additionalPatternsLayout = new AdditionalPatternsRepositoryLayout(fileResolver);
        this.layout = new GradleRepositoryLayout();
    }

    public DependencyResolver createResolver() {
        URI uri = getUrl();

        Set<String> schemes = new LinkedHashSet<String>();
        layout.addSchemes(uri, schemes);
        additionalPatternsLayout.addSchemes(uri, schemes);

        PatternBasedResolver resolver = createResolver(schemes);

        layout.apply(uri, resolver);
        additionalPatternsLayout.apply(uri, resolver);

        return resolver;
    }

    private PatternBasedResolver createResolver(Set<String> schemes) {
        if (schemes.isEmpty()) {
            throw new InvalidUserDataException("You must specify a base url or at least one artifact pattern for an Ivy repository.");
        }
        if (!WrapUtil.toSet("http", "https", "file").containsAll(schemes)) {
            throw new InvalidUserDataException("You may only specify 'file', 'http' and 'https' urls for an ivy repository.");
        }
        if (WrapUtil.toSet("http", "https").containsAll(schemes)) {
            return new IvyResolver(name, transportFactory.createHttpTransport(name, getCredentials()), locallyAvailableResourceFinder, cachedExternalResourceIndex);
        }
        if (WrapUtil.toSet("file").containsAll(schemes)) {
            return new IvyResolver(name, transportFactory.createFileTransport(name), locallyAvailableResourceFinder, cachedExternalResourceIndex);
        }
        throw new InvalidUserDataException("You cannot mix file and http(s) urls for a single ivy repository. Please declare 2 separate repositories.");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URI getUrl() {
        return baseUrl == null ? null : fileResolver.resolveUri(baseUrl);
    }

    public void setUrl(Object url) {
        baseUrl = url;
    }

    public void artifactPattern(String pattern) {
        additionalPatternsLayout.artifactPatterns.add(pattern);
    }

    public void ivyPattern(String pattern) {
        additionalPatternsLayout.ivyPatterns.add(pattern);
    }

    public void layout(String layoutName) {
        if ("maven".equals(layoutName)) {
            layout = new MavenRepositoryLayout();
        } else if ("pattern".equals(layoutName)) {
            layout = new PatternRepositoryLayout();
        } else {
            layout = new GradleRepositoryLayout();
        }
    }

    public void layout(String layoutName, Closure config) {
        layout(layoutName);
        ConfigureUtil.configure(config, layout);
    }

    /**
     * Layout for applying additional patterns added via {@link #artifactPatterns} and {@link #ivyPatterns}.
     */
    private static class AdditionalPatternsRepositoryLayout extends RepositoryLayout {
        private final FileResolver fileResolver;
        private final Set<String> artifactPatterns = new LinkedHashSet<String>();
        private final Set<String> ivyPatterns = new LinkedHashSet<String>();

        public AdditionalPatternsRepositoryLayout(FileResolver fileResolver) {
            this.fileResolver = fileResolver;
        }

        public void apply(URI baseUri, PatternBasedResolver resolver) {
            for (String artifactPattern : artifactPatterns) {
                ResolvedPattern resolvedPattern = new ResolvedPattern(artifactPattern, fileResolver);
                resolver.addArtifactLocation(resolvedPattern.baseUri, resolvedPattern.pattern);
            }

            Set<String> usedIvyPatterns = ivyPatterns.isEmpty() ? artifactPatterns : ivyPatterns;
            for (String ivyPattern : usedIvyPatterns) {
                ResolvedPattern resolvedPattern = new ResolvedPattern(ivyPattern, fileResolver);
                resolver.addDescriptorLocation(resolvedPattern.baseUri, resolvedPattern.pattern);
            }
        }

        @Override
        public void addSchemes(URI baseUri, Set<String> schemes) {
            for (String pattern : artifactPatterns) {
                schemes.add(new ResolvedPattern(pattern, fileResolver).scheme);
            }
            for (String pattern : ivyPatterns) {
                schemes.add(new ResolvedPattern(pattern, fileResolver).scheme);
            }
        }
    }

}
