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
package org.gradle.messaging.remote.internal;

import org.gradle.messaging.remote.internal.inet.InetEndpoint;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class DefaultMessageSerializer<T> implements MessageSerializer<T> {
    private final ClassLoader classLoader;

    public DefaultMessageSerializer(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public T read(DataInputStream inputStream, InetEndpoint localAddress, InetEndpoint remoteAddress) throws Exception {
        return (T) Message.receive(inputStream, classLoader);
    }

    public void write(T message, DataOutputStream outputStream) throws Exception {
        Message.send(message, outputStream);
    }
}
