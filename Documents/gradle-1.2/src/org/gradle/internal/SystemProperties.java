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
package org.gradle.internal;

import java.util.Map;

/**
 * Provides access to frequently used system properties.
 */
public class SystemProperties {
    @SuppressWarnings("unchecked")
    public static Map<String, String> asMap() {
        return (Map) System.getProperties();
    }

    public static String getLineSeparator() {
        return System.getProperty("line.separator");
    }

    public static String getJavaIoTmpDir() {
        return System.getProperty("java.io.tmpdir");
    }

    public static String getUserHome() {
        return System.getProperty("user.home");
    }

    public static String getJavaVersion() {
        return System.getProperty("java.version");
    }
}
