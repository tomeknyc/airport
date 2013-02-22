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
package org.gradle.api.publication.maven.internal.model

import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository
import org.gradle.api.internal.artifacts.repositories.DefaultPasswordCredentials
import org.gradle.api.internal.file.IdentityFileResolver
import org.gradle.api.publication.maven.MavenArtifact
import org.gradle.api.publication.maven.MavenDependency
import org.gradle.api.publication.maven.MavenPomCustomizer
import org.gradle.api.publication.maven.MavenPublication
import org.gradle.util.ConfigureUtil

class DefaultMavenPublication implements MavenPublication {
    String modelVersion
    String groupId
    String artifactId
    String version
    String packaging
    String description
    MavenArtifact mainArtifact
    List<MavenArtifact> subArtifacts = []
    List<MavenDependency> dependencies = []
    MavenPomCustomizer pom
    MavenArtifactRepository repository = new DefaultMavenArtifactRepository(new IdentityFileResolver(), new DefaultPasswordCredentials(), null, null, null)

    void repository(Closure c) {
        ConfigureUtil.configure(c, getRepository())
    }
}