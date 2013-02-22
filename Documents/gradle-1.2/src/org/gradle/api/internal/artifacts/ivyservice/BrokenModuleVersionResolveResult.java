/*
 * Copyright 2012 the original author or authors.
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

import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.module.id.ModuleRevisionId;

public class BrokenModuleVersionResolveResult implements ModuleVersionResolveResult {
    private final ModuleVersionResolveException failure;

    public BrokenModuleVersionResolveResult(ModuleVersionResolveException failure) {
        this.failure = failure;
    }

    public ModuleVersionResolveException getFailure() {
        return failure;
    }

    public ModuleRevisionId getId() throws ModuleVersionResolveException {
        throw failure;
    }

    public ModuleDescriptor getDescriptor() throws ModuleVersionResolveException {
        throw failure;
    }

    public ArtifactResolver getArtifactResolver() throws ModuleVersionResolveException {
        throw failure;
    }
}
