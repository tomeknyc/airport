/*
 * Copyright 2008 the original author or authors.
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
package org.gradle.api.tasks.diagnostics;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.diagnostics.internal.AsciiReportRenderer;
import org.gradle.api.tasks.diagnostics.internal.DependencyReportRenderer;
import org.gradle.api.tasks.diagnostics.internal.ReportRenderer;

import java.io.IOException;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Displays the dependency tree for a project. Can be configured to output to a file, and to optionally output a
 * graphviz compatible "dot" graph. An instance of this type is used when you execute the {@code dependencies} task from
 * the command-line.
 *
 * @author Phil Messenger
 */
public class DependencyReportTask extends AbstractReportTask {

    private DependencyReportRenderer renderer = new AsciiReportRenderer();

    private Set<Configuration> configurations;

    public ReportRenderer getRenderer() {
        return renderer;
    }

    /**
     * Set the renderer to use to build a report. If unset, AsciiGraphRenderer will be used.
     */
    public void setRenderer(DependencyReportRenderer renderer) {
        this.renderer = renderer;
    }

    public void generate(Project project) throws IOException {
        SortedSet<Configuration> sortedConfigurations = new TreeSet<Configuration>(new Comparator<Configuration>() {
            public int compare(Configuration conf1, Configuration conf2) {
                return conf1.getName().compareTo(conf2.getName());
            }
        });
        sortedConfigurations.addAll(getConfigurations(project));
        for (Configuration configuration : sortedConfigurations) {
            renderer.startConfiguration(configuration);
            renderer.render(configuration);
            renderer.completeConfiguration(configuration);
        }
    }

    private Set<Configuration> getConfigurations(Project project) {
        return configurations != null ? configurations : project.getConfigurations();
    }

    /**
     * Returns the configurations to generate the report for. Default to all configurations of this task's containing
     * project.
     *
     * @return the configurations.
     */
    public Set<Configuration> getConfigurations() {
        return configurations;
    }

    /**
     * Sets the configurations to generate the report for.
     *
     * @param configurations The configuration. Must not be null.
     */
    public void setConfigurations(Set<Configuration> configurations) {
        this.configurations = configurations;
    }
}
