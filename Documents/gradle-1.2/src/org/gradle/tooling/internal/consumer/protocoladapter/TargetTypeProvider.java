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

package org.gradle.tooling.internal.consumer.protocoladapter;

import org.gradle.tooling.model.idea.IdeaModuleDependency;
import org.gradle.tooling.model.idea.IdeaSingleEntryLibraryDependency;
import org.gradle.tooling.model.internal.outcomes.GradleFileBuildOutcome;

import java.util.HashMap;
import java.util.Map;

/**
 * by Szczepan Faber, created at: 4/2/12
 */
public class TargetTypeProvider {

    Map<String, Class<?>> configuredTargetTypes = new HashMap<String, Class<?>>();

    public TargetTypeProvider() {
        configuredTargetTypes.put(IdeaSingleEntryLibraryDependency.class.getCanonicalName(), IdeaSingleEntryLibraryDependency.class);
        configuredTargetTypes.put(IdeaModuleDependency.class.getCanonicalName(), IdeaModuleDependency.class);
        configuredTargetTypes.put(GradleFileBuildOutcome.class.getCanonicalName(), GradleFileBuildOutcome.class);
    }

    /**
     * Occasionally we want to use preconfigured target type instead of passed target type.
     *
     * @param initialTargetType
     * @param protocolObject
     */
    public <T, S> Class<T> getTargetType(Class<T> initialTargetType, S protocolObject) {
        Class<?>[] interfaces = protocolObject.getClass().getInterfaces();
        for (Class<?> i : interfaces) {
            if (configuredTargetTypes.containsKey(i.getName())) {
                return (Class<T>) configuredTargetTypes.get(i.getName());
            }
        }

        return initialTargetType;
    }
}