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

package org.gradle.api.tasks.diagnostics.internal.dependencies;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.result.ResolvedDependencyResult;
import org.gradle.api.artifacts.result.ResolvedModuleVersionResult;
import org.gradle.api.internal.artifacts.result.DefaultResolvedModuleVersionResult;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * by Szczepan Faber, created at: 8/10/12
 */
public class RenderableModuleResult implements RenderableDependency {

    private final ResolvedModuleVersionResult module;

    public RenderableModuleResult(ResolvedModuleVersionResult module) {
        this.module = module;
    }

    public ModuleVersionIdentifier getId() {
        return module.getId();
    }

    public String getName() {
        return module.getId().getGroup() + ":" + module.getId().getName() + ":" + module.getId().getVersion();
    }

    public String getDescription() {
        return getName();
    }

    public Set<RenderableDependency> getChildren() {
        return new LinkedHashSet(Collections2.transform(module.getDependencies(), new Function<ResolvedDependencyResult, RenderableDependency>() {
            public RenderableDependency apply(ResolvedDependencyResult input) {
                return new RenderableDependencyResult(input);
            }
        }));
    }

    public Set<RenderableDependency> getParents() {
        return new LinkedHashSet(Collections2.transform(((DefaultResolvedModuleVersionResult) module).getDependees(), new Function<ResolvedModuleVersionResult, RenderableDependency>() {
            public RenderableDependency apply(ResolvedModuleVersionResult input) {
                return new RenderableModuleResult(input);
            }
        }));
    }
}
