/*
 * Copyright 2012 the original author or authors.
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

package org.gradle.tooling.internal.consumer.connection;

import org.gradle.tooling.internal.consumer.parameters.ConsumerOperationParameters;
import org.gradle.tooling.internal.protocol.ConnectionVersion4;
import org.gradle.tooling.internal.protocol.InternalConnection;

public class InternalConnectionBackedConsumerConnection extends AdaptedConnection {
    private final InternalConnection connection;

    public InternalConnectionBackedConsumerConnection(ConnectionVersion4 delegate) {
        super(delegate);
        connection = (InternalConnection) delegate;
    }

    @Override
    protected <T> T doGetModel(Class<T> type, ConsumerOperationParameters operationParameters) {
        return connection.getTheModel(type, operationParameters);
    }
}
