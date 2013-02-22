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

package org.gradle.tooling.internal.protocol;

/**
 * by Szczepan Faber, created at: 1/1/12
 *
 * @deprecated Use {@link BuildActionRunner} instead.
 */
@Deprecated
public interface InternalConnection extends ConnectionVersion4, InternalProtocolInterface {
    /**
     * Fetches a snapshot of the model for the project. This method is generic so that we're not locked
     * to building particular model type.
     * <p>
     * The other method on the interface, e.g. {@link #getModel(Class, BuildOperationParametersVersion1)} should be considered deprecated
     *
     * @throws UnsupportedOperationException When the given model type is not supported.
     * @throws IllegalStateException When this connection has been stopped.
     * @deprecated Use {@link BuildActionRunner#run(Class, BuildOperationParametersVersion1)} instead.
     */
    @Deprecated
    <T> T getTheModel(Class<T> type, BuildOperationParametersVersion1 operationParameters) throws UnsupportedOperationException, IllegalStateException;
}
