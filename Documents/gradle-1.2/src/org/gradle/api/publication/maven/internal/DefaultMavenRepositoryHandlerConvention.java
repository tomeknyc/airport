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
package org.gradle.api.publication.maven.internal;

import groovy.lang.Closure;
import org.gradle.api.artifacts.maven.GroovyMavenDeployer;
import org.gradle.api.artifacts.maven.MavenResolver;
import org.gradle.api.internal.artifacts.dsl.DefaultRepositoryHandler;
import org.gradle.api.plugins.MavenRepositoryHandlerConvention;

import java.util.Map;

public class DefaultMavenRepositoryHandlerConvention implements MavenRepositoryHandlerConvention {
    private final DefaultRepositoryHandler container;
    private final DeployerFactory deployerFactory;

    public DefaultMavenRepositoryHandlerConvention(DefaultRepositoryHandler container, DeployerFactory deployerFactory) {
        this.container = container;
        this.deployerFactory = deployerFactory;
    }

    public GroovyMavenDeployer mavenDeployer() {
        return container.addRepository(createMavenDeployer(), DEFAULT_MAVEN_DEPLOYER_NAME);
    }

    public GroovyMavenDeployer mavenDeployer(Closure configureClosure) {
        return container.addRepository(createMavenDeployer(), configureClosure, DEFAULT_MAVEN_DEPLOYER_NAME);
    }

    public GroovyMavenDeployer mavenDeployer(Map<String, ?> args) {
        return container.addRepository(createMavenDeployer(), args, DEFAULT_MAVEN_DEPLOYER_NAME);
    }

    public GroovyMavenDeployer mavenDeployer(Map<String, ?> args, Closure configureClosure) {
        return container.addRepository(createMavenDeployer(), args, configureClosure, DEFAULT_MAVEN_DEPLOYER_NAME);
    }

    private GroovyMavenDeployer createMavenDeployer() {
        return deployerFactory.createMavenDeployer();
    }

    public MavenResolver mavenInstaller() {
        return container.addRepository(createMavenInstaller(), DEFAULT_MAVEN_INSTALLER_NAME);
    }

    public MavenResolver mavenInstaller(Closure configureClosure) {
        return container.addRepository(createMavenInstaller(), configureClosure, DEFAULT_MAVEN_INSTALLER_NAME);
    }

    public MavenResolver mavenInstaller(Map<String, ?> args) {
        return container.addRepository(createMavenInstaller(), args, DEFAULT_MAVEN_INSTALLER_NAME);
    }

    public MavenResolver mavenInstaller(Map<String, ?> args, Closure configureClosure) {
        return container.addRepository(createMavenInstaller(), args, configureClosure, DEFAULT_MAVEN_INSTALLER_NAME);
    }

    private MavenResolver createMavenInstaller() {
        return deployerFactory.createMavenInstaller();
    }
}
