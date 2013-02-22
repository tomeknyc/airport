/*
 * Copyright 2007-2008 the original author or authors.
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
package org.gradle.api.internal.artifacts.ivyservice;

import org.apache.ivy.Ivy;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.plugins.resolver.DependencyResolver;
import org.gradle.api.UncheckedIOException;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Module;
import org.gradle.api.artifacts.PublishException;
import org.gradle.api.internal.artifacts.ArtifactPublisher;
import org.gradle.api.internal.artifacts.configurations.ConfigurationInternal;
import org.gradle.api.internal.artifacts.configurations.Configurations;
import org.gradle.api.internal.artifacts.configurations.ResolverProvider;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Set;

/**
 * @author Hans Dockter
 */
public class IvyBackedArtifactPublisher implements ArtifactPublisher {
    private final SettingsConverter settingsConverter;
    private final ModuleDescriptorConverter publishModuleDescriptorConverter;
    private final ModuleDescriptorConverter fileModuleDescriptorConverter;
    private final IvyFactory ivyFactory;
    private final IvyDependencyPublisher dependencyPublisher;
    private final ResolverProvider resolverProvider;

    public IvyBackedArtifactPublisher(ResolverProvider resolverProvider,
                                      SettingsConverter settingsConverter,
                                      ModuleDescriptorConverter publishModuleDescriptorConverter,
                                      ModuleDescriptorConverter fileModuleDescriptorConverter,
                                      IvyFactory ivyFactory,
                                      IvyDependencyPublisher dependencyPublisher) {
        this.resolverProvider = resolverProvider;
        this.settingsConverter = settingsConverter;
        this.publishModuleDescriptorConverter = publishModuleDescriptorConverter;
        this.fileModuleDescriptorConverter = fileModuleDescriptorConverter;
        this.ivyFactory = ivyFactory;
        this.dependencyPublisher = dependencyPublisher;
    }

    private Ivy ivyForPublish(List<DependencyResolver> publishResolvers) {
        return ivyFactory.createIvy(settingsConverter.convertForPublish(publishResolvers));
    }

    public void publish(ConfigurationInternal configuration, File descriptorDestination) throws PublishException {
        List<DependencyResolver> publishResolvers = resolverProvider.getResolvers();
        Ivy ivy = ivyForPublish(publishResolvers);
        Set<Configuration> configurationsToPublish = configuration.getHierarchy();
        Set<String> confs = Configurations.getNames(configurationsToPublish, false);
        writeDescriptorFile(descriptorDestination, configurationsToPublish, configuration.getModule());
        dependencyPublisher.publish(
                confs,
                publishResolvers,
                publishModuleDescriptorConverter.convert(configurationsToPublish, configuration.getModule()),
                descriptorDestination,
                ivy.getEventManager());
    }

    private void writeDescriptorFile(File descriptorDestination, Set<Configuration> configurationsToPublish, Module module) {
        if (descriptorDestination == null) {
            return;
        }
        assert configurationsToPublish.size() > 0;
        Set<Configuration> allConfigurations = configurationsToPublish.iterator().next().getAll();
        ModuleDescriptor moduleDescriptor = fileModuleDescriptorConverter.convert(allConfigurations, module);
        try {
            moduleDescriptor.toIvyFile(descriptorDestination);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
