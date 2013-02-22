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
package org.gradle.integtests.fixtures;

import org.gradle.internal.jvm.Jvm;
import org.gradle.internal.os.OperatingSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * A base class for those test runners which execute a test multiple times against a set of Gradle versions.
 */
public abstract class AbstractCompatibilityTestRunner extends AbstractMultiTestRunner {
    protected final GradleDistribution current = new GradleDistribution();
    protected final List<BasicGradleDistribution> previous;

    protected AbstractCompatibilityTestRunner(Class<?> target) {
        this(target, null);
    }

    protected AbstractCompatibilityTestRunner(Class<?> target, String versionStr) {
        super(target);
        validateTestName(target);

        previous = new ArrayList<BasicGradleDistribution>();
        if (versionStr == null) {
            versionStr = System.getProperty("org.gradle.integtest.versions", "latest");
        }
        ReleasedVersions previousVersions = new ReleasedVersions(current);
        if (!versionStr.equals("all")) {
            previous.add(previousVersions.getLast());
        } else {
            List<BasicGradleDistribution> all = previousVersions.getAll();
            for (BasicGradleDistribution previous : all) {
                if (!previous.worksWith(Jvm.current())) {
                    add(new IgnoredVersion(previous, "does not work with current JVM"));
                    continue;
                }
                if (!previous.worksWith(OperatingSystem.current())) {
                    add(new IgnoredVersion(previous, "does not work with current OS"));
                    continue;
                }
                this.previous.add(previous);
            }
        }
    }

    /**
     * Makes sure the test adhers to the naming convention.
     *
     * @param target test class
     */
    private void validateTestName(Class<?> target) {
        if (!target.getSimpleName().contains("CrossVersion")) {
            throw new RuntimeException("The tests that use " + this.getClass().getSimpleName()
                    + " must follow a certain naming convention, e.g. name must contain 'CrossVersion' substring.\n"
                    + "This way we can include/exclude those test nicely and it is easier to configure the CI.\n"
                    + "Please include 'CrossVersion' in the name of the test: '" + target.getSimpleName() + "'");
        }
    }

    public List<BasicGradleDistribution> getPrevious() {
        return previous;
    }

    private static class IgnoredVersion extends Execution {
        private final BasicGradleDistribution distribution;
        private final String why;

        private IgnoredVersion(BasicGradleDistribution distribution, String why) {
            this.distribution = distribution;
            this.why = why;
        }

        @Override
        protected boolean isEnabled() {
            return false;
        }

        @Override
        protected String getDisplayName() {
            return String.format("%s %s", distribution.getVersion(), why);
        }
    }
}
