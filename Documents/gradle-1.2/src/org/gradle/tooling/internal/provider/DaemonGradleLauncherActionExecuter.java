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
package org.gradle.tooling.internal.provider;

import org.gradle.configuration.GradleLauncherMetaData;
import org.gradle.initialization.GradleLauncherAction;
import org.gradle.launcher.daemon.configuration.DaemonParameters;
import org.gradle.launcher.exec.BuildActionParameters;
import org.gradle.launcher.exec.DefaultBuildActionParameters;
import org.gradle.launcher.exec.GradleLauncherActionExecuter;
import org.gradle.launcher.exec.ReportedException;
import org.gradle.tooling.internal.protocol.BuildExceptionVersion1;
import org.gradle.tooling.internal.provider.connection.ProviderOperationParameters;

import java.io.File;

public class DaemonGradleLauncherActionExecuter implements GradleLauncherActionExecuter<ProviderOperationParameters> {
    private final GradleLauncherActionExecuter<BuildActionParameters> executer;
    private final DaemonParameters parameters;

    public DaemonGradleLauncherActionExecuter(GradleLauncherActionExecuter<BuildActionParameters> executer, DaemonParameters parameters) {
        this.executer = executer;
        this.parameters = parameters;
    }

    public <T> T execute(GradleLauncherAction<T> action, ProviderOperationParameters actionParameters) {
        BuildActionParameters parameters = new DefaultBuildActionParameters(new GradleLauncherMetaData(), actionParameters.getStartTime(),
                this.parameters.getEffectiveSystemProperties(), System.getenv(), new File(System.getProperty("user.dir")), actionParameters.getBuildLogLevel());
        try {
            return executer.execute(action, parameters);
        } catch (ReportedException e) {
            throw new BuildExceptionVersion1(e.getCause());
        }
    }
}
