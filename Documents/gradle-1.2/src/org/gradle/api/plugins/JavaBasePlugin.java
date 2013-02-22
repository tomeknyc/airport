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

package org.gradle.api.plugins;

import org.gradle.api.*;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.internal.ConventionMapping;
import org.gradle.api.internal.IConventionAware;
import org.gradle.api.internal.plugins.ProcessResources;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.reporting.ReportingExtension;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.compile.AbstractCompile;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.api.tasks.testing.Test;
import org.gradle.api.tasks.testing.TestDescriptor;
import org.gradle.api.tasks.testing.TestListener;
import org.gradle.api.tasks.testing.TestResult;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.util.WrapUtil;

import javax.inject.Inject;
import java.io.File;
import java.util.concurrent.Callable;

/**
 * <p>A {@link org.gradle.api.Plugin} which compiles and tests Java source, and assembles it into a JAR file.</p>
 *
 * @author Hans Dockter
 */
public class JavaBasePlugin implements Plugin<Project> {
    public static final String CHECK_TASK_NAME = "check";
    public static final String BUILD_TASK_NAME = "build";
    public static final String BUILD_DEPENDENTS_TASK_NAME = "buildDependents";
    public static final String BUILD_NEEDED_TASK_NAME = "buildNeeded";
    public static final String VERIFICATION_GROUP = "verification";
    public static final String DOCUMENTATION_GROUP = "documentation";

    private final Instantiator instantiator;

    @Inject
    public JavaBasePlugin(Instantiator instantiator) {
        this.instantiator = instantiator;
    }

    public void apply(Project project) {
        project.getPlugins().apply(BasePlugin.class);
        project.getPlugins().apply(ReportingBasePlugin.class);

        JavaPluginConvention javaConvention = new JavaPluginConvention((ProjectInternal) project, instantiator);
        project.getConvention().getPlugins().put("java", javaConvention);

        configureCompileDefaults(project, javaConvention);
        configureSourceSetDefaults(javaConvention);

        configureJavaDoc(project, javaConvention);
        configureTest(project, javaConvention);
        configureCheck(project);
        configureBuild(project);
        configureBuildNeeded(project);
        configureBuildDependents(project);
    }

    private void configureSourceSetDefaults(final JavaPluginConvention pluginConvention) {
        pluginConvention.getSourceSets().all(new Action<SourceSet>() {
            public void execute(final SourceSet sourceSet) {
                final Project project = pluginConvention.getProject();

                ConventionMapping outputConventionMapping = ((IConventionAware) sourceSet.getOutput()).getConventionMapping();

                ConfigurationContainer configurations = project.getConfigurations();

                Configuration compileConfiguration = configurations.findByName(sourceSet.getCompileConfigurationName());
                if (compileConfiguration == null) {
                    compileConfiguration = configurations.add(sourceSet.getCompileConfigurationName());
                }
                compileConfiguration.setVisible(false);
                compileConfiguration.setDescription(String.format("Classpath for compiling the %s sources.", sourceSet.getName()));

                Configuration runtimeConfiguration = configurations.findByName(sourceSet.getRuntimeConfigurationName());
                if (runtimeConfiguration == null) {
                    runtimeConfiguration = configurations.add(sourceSet.getRuntimeConfigurationName());
                }
                runtimeConfiguration.setVisible(false);
                runtimeConfiguration.extendsFrom(compileConfiguration);
                runtimeConfiguration.setDescription(String.format("Classpath for running the compiled %s classes.", sourceSet.getName()));

                sourceSet.setCompileClasspath(compileConfiguration);
                sourceSet.setRuntimeClasspath(sourceSet.getOutput().plus(runtimeConfiguration));

                outputConventionMapping.map("classesDir", new Callable<Object>() {
                    public Object call() throws Exception {
                        String classesDirName = String.format("classes/%s", sourceSet.getName());
                        return new File(project.getBuildDir(), classesDirName);
                    }
                });
                outputConventionMapping.map("resourcesDir", new Callable<Object>() {
                    public Object call() throws Exception {
                        String classesDirName = String.format("resources/%s", sourceSet.getName());
                        return new File(project.getBuildDir(), classesDirName);
                    }
                });

                sourceSet.getJava().srcDir(String.format("src/%s/java", sourceSet.getName()));
                sourceSet.getResources().srcDir(String.format("src/%s/resources", sourceSet.getName()));

                Copy processResources = project.getTasks().add(sourceSet.getProcessResourcesTaskName(), ProcessResources.class);
                processResources.setDescription(String.format("Processes the %s.", sourceSet.getResources()));
                ConventionMapping conventionMapping = processResources.getConventionMapping();
                processResources.from(sourceSet.getResources());
                conventionMapping.map("destinationDir", new Callable<Object>() {
                    public Object call() throws Exception {
                        return sourceSet.getOutput().getResourcesDir();
                    }
                });

                String compileTaskName = sourceSet.getCompileJavaTaskName();
                JavaCompile compileJava = project.getTasks().add(compileTaskName, JavaCompile.class);
                configureForSourceSet(sourceSet, compileJava);

                Task classes = project.getTasks().add(sourceSet.getClassesTaskName());
                classes.dependsOn(sourceSet.getProcessResourcesTaskName(), compileTaskName);
                classes.setDescription(String.format("Assembles the %s classes.", sourceSet.getName()));
                classes.setGroup(BasePlugin.BUILD_GROUP);
                classes.dependsOn(sourceSet.getOutput().getDirs());

                sourceSet.compiledBy(sourceSet.getClassesTaskName());
            }
        });
    }

    public void configureForSourceSet(final SourceSet sourceSet, AbstractCompile compile) {
        ConventionMapping conventionMapping;
        compile.setDescription(String.format("Compiles the %s.", sourceSet.getJava()));
        conventionMapping = compile.getConventionMapping();
        compile.setSource(sourceSet.getJava());
        conventionMapping.map("classpath", new Callable<Object>() {
            public Object call() throws Exception {
                return sourceSet.getCompileClasspath();
            }
        });
        conventionMapping.map("destinationDir", new Callable<Object>() {
            public Object call() throws Exception {
                return sourceSet.getOutput().getClassesDir();
            }
        });
    }

    private void configureCompileDefaults(final Project project, final JavaPluginConvention javaConvention) {
        project.getTasks().withType(AbstractCompile.class, new Action<AbstractCompile>() {
            public void execute(final AbstractCompile compile) {
                ConventionMapping conventionMapping = compile.getConventionMapping();
                conventionMapping.map("sourceCompatibility", new Callable<Object>() {
                    public Object call() throws Exception {
                        return javaConvention.getSourceCompatibility().toString();
                    }
                });
                conventionMapping.map("targetCompatibility", new Callable<Object>() {
                    public Object call() throws Exception {
                        return javaConvention.getTargetCompatibility().toString();
                    }
                });
            }
        });
        project.getTasks().withType(JavaCompile.class, new Action<JavaCompile>() {
            public void execute(final JavaCompile compile) {
                ConventionMapping conventionMapping = compile.getConventionMapping();
                conventionMapping.map("dependencyCacheDir", new Callable<Object>() {
                    public Object call() throws Exception {
                        return javaConvention.getDependencyCacheDir();
                    }
                });
            }
        });
    }

    private void configureJavaDoc(final Project project, final JavaPluginConvention convention) {
        project.getTasks().withType(Javadoc.class, new Action<Javadoc>() {
            public void execute(Javadoc javadoc) {
                javadoc.getConventionMapping().map("destinationDir", new Callable<Object>() {
                    public Object call() throws Exception {
                        return new File(convention.getDocsDir(), "javadoc");
                    }
                });
                javadoc.getConventionMapping().map("title", new Callable<Object>() {
                    public Object call() throws Exception {
                        return project.getExtensions().getByType(ReportingExtension.class).getApiDocTitle();
                    }
                });
            }
        });
    }

    private void configureCheck(final Project project) {
        Task checkTask = project.getTasks().add(CHECK_TASK_NAME);
        checkTask.setDescription("Runs all checks.");
        checkTask.setGroup(VERIFICATION_GROUP);
    }

    private void configureBuild(Project project) {
        DefaultTask buildTask = project.getTasks().add(BUILD_TASK_NAME, DefaultTask.class);
        buildTask.setDescription("Assembles and tests this project.");
        buildTask.setGroup(BasePlugin.BUILD_GROUP);
        buildTask.dependsOn(BasePlugin.ASSEMBLE_TASK_NAME);
        buildTask.dependsOn(CHECK_TASK_NAME);
    }

    private void configureBuildNeeded(Project project) {
        DefaultTask buildTask = project.getTasks().add(BUILD_NEEDED_TASK_NAME, DefaultTask.class);
        buildTask.setDescription("Assembles and tests this project and all projects it depends on.");
        buildTask.setGroup(BasePlugin.BUILD_GROUP);
        buildTask.dependsOn(BUILD_TASK_NAME);
    }

    private void configureBuildDependents(Project project) {
        DefaultTask buildTask = project.getTasks().add(BUILD_DEPENDENTS_TASK_NAME, DefaultTask.class);
        buildTask.setDescription("Assembles and tests this project and all projects that depend on it.");
        buildTask.setGroup(BasePlugin.BUILD_GROUP);
        buildTask.dependsOn(BUILD_TASK_NAME);
    }

    private void configureTest(final Project project, final JavaPluginConvention convention) {
        project.getTasks().withType(Test.class, new Action<Test>() {
            public void execute(Test test) {
                configureTestDefaults(test, project, convention);
            }
        });
        project.afterEvaluate(new Action<Project>() {
            public void execute(Project project) {
                project.getTasks().withType(Test.class, new Action<Test>() {
                    public void execute(Test test) {
                        overwriteIncludesIfSinglePropertyIsSet(test);
                        overwriteDebugIfDebugPropertyIsSet(test);
                    }
                });
            }
        });
    }

    private void overwriteDebugIfDebugPropertyIsSet(Test test) {
        String debugProp = getTaskPrefixedProperty(test, "debug");
        if (debugProp != null) {
            test.doFirst(new Action<Task>() {
                public void execute(Task task) {
                    task.getLogger().info("Running tests for remote debugging.");
                }
            });
            test.setDebug(true);
        }
    }

    private void overwriteIncludesIfSinglePropertyIsSet(final Test test) {
        String singleTest = getTaskPrefixedProperty(test, "single");
        if (singleTest == null) {
            return;
        }
        test.doFirst(new Action<Task>() {
            public void execute(Task task) {
                test.getLogger().info("Running single tests with pattern: {}", test.getIncludes());
            }
        });
        test.setIncludes(WrapUtil.toSet(String.format("**/%s*.class", singleTest)));
        failIfNoTestIsExecuted(test, singleTest);
    }

    private void failIfNoTestIsExecuted(Test test, final String pattern) {
        test.addTestListener(new TestListener() {
            public void beforeSuite(TestDescriptor suite) {
                // do nothing
            }

            public void afterSuite(TestDescriptor suite, TestResult result) {
                if (suite.getParent() == null && result.getTestCount() == 0) {
                    throw new GradleException("Could not find matching test for pattern: " + pattern);
                }
            }

            public void beforeTest(TestDescriptor testDescriptor) {
                // do nothing
            }

            public void afterTest(TestDescriptor testDescriptor, TestResult result) {
                // do nothing
            }
        });
    }

    private String getTaskPrefixedProperty(Task task, String propertyName) {
        String suffix = '.' + propertyName;
        String value = System.getProperty(task.getPath() + suffix);
        if (value == null) {
            return System.getProperty(task.getName() + suffix);
        }
        return value;
    }

    private void configureTestDefaults(Test test, Project project, final JavaPluginConvention convention) {
        test.getConventionMapping().map("testResultsDir", new Callable<Object>() {
            public Object call() throws Exception {
                return convention.getTestResultsDir();
            }
        });
        test.getConventionMapping().map("testReportDir", new Callable<Object>() {
            public Object call() throws Exception {
                return convention.getTestReportDir();
            }
        });
        test.workingDir(project.getProjectDir());
    }
}