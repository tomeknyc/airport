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
package org.gradle.tooling.model;

import org.gradle.api.Nullable;

/**
 * Represents a task which is executable by Gradle.
 */
public interface Task {
    /**
     * Returns the path of this task. This is a fully qualified unique name for this task.
     *
     * @return The path of this task.
     */
    String getPath();

    /**
     * Returns the name of this task. Note that the name is not necessarily a unique identifier for the task.
     *
     * @return The name of this task.
     */
    String getName();

    /**
     * Returns the description of this task, or {@code null} if it has no description.
     *
     * @return The description of this task, or {@code null} if it has no description.
     */
    @Nullable
    String getDescription();

    /**
     * Returns the element which this task belongs to.
     *
     * @return The element which this task belongs to.
     */
    Element getProject();
    //TODO SF rename to 'owner'? I'd deprecate the Task interface and leave only GradleTask (or push this method down to the GradleTask)
}
