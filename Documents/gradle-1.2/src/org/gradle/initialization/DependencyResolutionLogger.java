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
package org.gradle.initialization;

import org.gradle.api.artifacts.DependencyResolutionListener;
import org.gradle.api.artifacts.ResolvableDependencies;
import org.gradle.logging.ProgressLogger;
import org.gradle.logging.ProgressLoggerFactory;

// TODO:DAZ Think about a better way to do thread-safety here, maybe
public class DependencyResolutionLogger implements DependencyResolutionListener {
    private final ProgressLoggerFactory loggerFactory;
    private final ThreadLocal<ProgressLogger> progressLogger = new ThreadLocal<ProgressLogger>();

    public DependencyResolutionLogger(ProgressLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    public void beforeResolve(ResolvableDependencies dependencies) {
        checkLogger(false);
        ProgressLogger logger = loggerFactory.newOperation(DependencyResolutionLogger.class);
        logger.setDescription(String.format("Resolve %s", dependencies));
        logger.setShortDescription(String.format("Resolving %s", dependencies));
        logger.started();
        progressLogger.set(logger);
    }

    public void afterResolve(ResolvableDependencies dependencies) {
        checkLogger(true);
        progressLogger.get().completed();
        progressLogger.remove();
    }

    private void checkLogger(boolean shouldExist) {
        ProgressLogger logger = progressLogger.get();
        if (shouldExist && logger == null) {
            throw new IllegalStateException("Logging operation not started");
        }
        if (!shouldExist && logger != null) {
            throw new IllegalStateException("Logging operation already in progress");
        }
    }
}
