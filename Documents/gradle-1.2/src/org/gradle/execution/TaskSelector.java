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
package org.gradle.execution;

import com.google.common.collect.SetMultimap;
import org.apache.commons.lang.StringUtils;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.internal.GradleInternal;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.util.NameMatcher;

import java.util.Map;
import java.util.Set;

public class TaskSelector {
    private final TaskNameResolver taskNameResolver;
    private Set<Task> tasks;
    private String taskName;

    public TaskSelector() {
        this(new TaskNameResolver());
    }

    public TaskSelector(TaskNameResolver taskNameResolver) {
        this.taskNameResolver = taskNameResolver;
    }

    public void selectTasks(GradleInternal gradle, String path) {
        SetMultimap<String, Task> tasksByName;
        String baseName;
        String prefix;

        ProjectInternal project = gradle.getDefaultProject();

        if (path.contains(Project.PATH_SEPARATOR)) {
            String projectPath = StringUtils.substringBeforeLast(path, Project.PATH_SEPARATOR);
            projectPath = projectPath.length() == 0 ? Project.PATH_SEPARATOR : projectPath;
            project = findProject(project, projectPath);
            baseName = StringUtils.substringAfterLast(path, Project.PATH_SEPARATOR);
            prefix = project.getPath() + Project.PATH_SEPARATOR;

            tasksByName = taskNameResolver.select(baseName, project);
        } else {
            baseName = path;
            prefix = "";

            tasksByName = taskNameResolver.selectAll(path, project);
        }

        Set<Task> tasks = tasksByName.get(baseName);
        if (!tasks.isEmpty()) {
            // An exact match
            this.tasks = tasks;
            this.taskName = path;
            return;
        }

        NameMatcher matcher = new NameMatcher();
        String actualName = matcher.find(baseName, tasksByName.keySet());

        if (actualName != null) {
            // A partial match
            this.tasks = tasksByName.get(actualName);
            this.taskName = prefix + actualName;
            return;
        }

        throw new TaskSelectionException(matcher.formatErrorMessage("task", project));
    }

    public String getTaskName() {
        return taskName;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    private static ProjectInternal findProject(ProjectInternal startFrom, String path) {
        if (path.equals(Project.PATH_SEPARATOR)) {
            return startFrom.getRootProject();
        }
        Project current = startFrom;
        if (path.startsWith(Project.PATH_SEPARATOR)) {
            current = current.getRootProject();
            path = path.substring(1);
        }
        for (String pattern : path.split(Project.PATH_SEPARATOR)) {
            Map<String, Project> children = current.getChildProjects();

            NameMatcher matcher = new NameMatcher();
            Project child = matcher.find(pattern, children);
            if (child != null) {
                current = child;
                continue;
            }

            throw new TaskSelectionException(matcher.formatErrorMessage("project", current));
        }

        return (ProjectInternal) current;
    }
}
