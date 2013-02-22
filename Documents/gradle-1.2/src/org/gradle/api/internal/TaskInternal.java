/*
 * Copyright 2010 the original author or authors.
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

package org.gradle.api.internal;

import org.gradle.api.Task;
import org.gradle.api.internal.tasks.TaskExecuter;
import org.gradle.api.internal.tasks.execution.TaskValidator;
import org.gradle.api.specs.Spec;
import org.gradle.internal.Factory;
import org.gradle.logging.StandardOutputCapture;
import org.gradle.util.Configurable;

import java.util.List;
import java.io.File;

public interface TaskInternal extends Task, Configurable<Task> {
    Spec<? super TaskInternal> getOnlyIf();

    void execute();

    void executeWithoutThrowingTaskFailure();

    StandardOutputCapture getStandardOutputCapture();

    TaskExecuter getExecuter();

    void setExecuter(TaskExecuter executer);

    TaskOutputsInternal getOutputs();

    List<TaskValidator> getValidators();

    void addValidator(TaskValidator validator);

    /**
     * The returned factory is expected to return the same file each time.
     * <p>
     * The getTemporaryDir() method creates the directory which can be problematic. Use this to delay that creation.
     */
    Factory<File> getTemporaryDirFactory();
}
