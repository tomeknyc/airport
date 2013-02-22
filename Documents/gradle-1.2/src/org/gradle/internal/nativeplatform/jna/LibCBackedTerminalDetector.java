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

package org.gradle.internal.nativeplatform.jna;

import org.gradle.internal.UncheckedException;
import org.gradle.internal.nativeplatform.TerminalDetector;

import java.io.FileDescriptor;
import java.lang.reflect.Field;

public class LibCBackedTerminalDetector implements TerminalDetector {
    private final LibC libC;

    public LibCBackedTerminalDetector(LibC libC) {
        this.libC = libC;
    }

    public boolean isTerminal(FileDescriptor fileDescriptor) {
        int osFileDesc;
        try {
            Field fdField = FileDescriptor.class.getDeclaredField("fd");
            fdField.setAccessible(true);
            osFileDesc = fdField.getInt(fileDescriptor);
        } catch (Exception e) {
            throw UncheckedException.throwAsUncheckedException(e);
        }

        // Determine if we're connected to a terminal
        if (libC.isatty(osFileDesc) == 0) {
            return false;
        }

        // Dumb terminal doesn't support ANSI control codes. Should really be using termcap database.
        String term = System.getenv("TERM");
        if (term != null && term.equals("dumb")) {
            return false;
        }

        // Assume a terminal
        return true;
    }
}
