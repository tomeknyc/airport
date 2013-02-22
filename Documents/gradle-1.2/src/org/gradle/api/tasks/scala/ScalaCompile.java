/*
 * Copyright 2009 the original author or authors.
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
package org.gradle.api.tasks.scala;

import org.gradle.api.AntBuilder;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.internal.project.IsolatedAntBuilder;
import org.gradle.api.internal.tasks.compile.AntJavaCompiler;
import org.gradle.api.internal.tasks.compile.JavaCompileSpec;
import org.gradle.api.internal.tasks.scala.*;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.compile.AbstractCompile;
import org.gradle.api.tasks.compile.CompileOptions;
import org.gradle.api.internal.tasks.compile.Compiler;

/**
 * Compiles Scala source files, and optionally, Java source files.
 */
public class ScalaCompile extends AbstractCompile {
    private FileCollection scalaClasspath;
    private Compiler<ScalaJavaJointCompileSpec> compiler;
    private final CompileOptions compileOptions = new CompileOptions();
    private final ScalaCompileOptions scalaCompileOptions = new ScalaCompileOptions();

    public ScalaCompile() {
        Compiler<ScalaCompileSpec> scalaCompiler = new AntScalaCompiler(getServices().get(IsolatedAntBuilder.class));
        Compiler<JavaCompileSpec> javaCompiler = new AntJavaCompiler(getServices().getFactory(AntBuilder.class));
        compiler = new IncrementalScalaCompiler(new DefaultScalaJavaJointCompiler(scalaCompiler, javaCompiler), getOutputs());
    }

    /**
     * Returns the classpath to use to load the Scala compiler.
     */
    @InputFiles
    public FileCollection getScalaClasspath() {
        return scalaClasspath;
    }

    public void setScalaClasspath(FileCollection scalaClasspath) {
        this.scalaClasspath = scalaClasspath;
    }

    public Compiler<ScalaJavaJointCompileSpec> getCompiler() {
        return compiler;
    }

    public void setCompiler(Compiler<ScalaJavaJointCompileSpec> compiler) {
        this.compiler = compiler;
    }

    /**
     * Returns the Scala compilation options.
     */
    @Nested
    public ScalaCompileOptions getScalaCompileOptions() {
        return scalaCompileOptions;
    }

    /**
     * Returns the Java compilation options.
     */
    @Nested
    public CompileOptions getOptions() {
        return compileOptions;
    }

    @Override
    protected void compile() {
        FileTree source = getSource();
        DefaultScalaJavaJointCompileSpec spec = new DefaultScalaJavaJointCompileSpec();
        spec.setSource(source);
        spec.setDestinationDir(getDestinationDir());
        spec.setClasspath(getClasspath());
        spec.setScalaClasspath(getScalaClasspath());
        spec.setSourceCompatibility(getSourceCompatibility());
        spec.setTargetCompatibility(getTargetCompatibility());
        spec.setCompileOptions(compileOptions);
        spec.setScalaCompileOptions(scalaCompileOptions);
        compiler.execute(spec);
    }
}
