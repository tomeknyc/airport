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

package org.gradle.process.internal;

import org.gradle.api.Action;
import org.gradle.internal.UncheckedException;
import org.gradle.messaging.remote.ConnectEvent;
import org.gradle.messaging.remote.ObjectConnection;
import org.gradle.process.ExecResult;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultWorkerProcess implements WorkerProcess {
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private ObjectConnection connection;
    private ExecHandle execHandle;
    private boolean running;
    private Throwable processFailure;
    private final long connectTimeout;

    public DefaultWorkerProcess(int connectTimeoutValue, TimeUnit connectTimeoutUnits) {
        connectTimeout = connectTimeoutUnits.toMillis(connectTimeoutValue);
    }

    public void setExecHandle(ExecHandle execHandle) {
        this.execHandle = execHandle;
        execHandle.addListener(new ExecHandleListener() {
            public void executionStarted(ExecHandle execHandle) {
            }

            public void executionFinished(ExecHandle execHandle, ExecResult execResult) {
                onProcessStop(execResult);
            }
        });
    }

    public Action<ConnectEvent<ObjectConnection>> getConnectAction() {
        return new Action<ConnectEvent<ObjectConnection>>() {
            public void execute(ConnectEvent<ObjectConnection> event) {
                onConnect(event.getConnection());
            }
        };
    }

    private void onConnect(ObjectConnection connection) {
        lock.lock();
        try {
            this.connection = connection;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private void onProcessStop(ExecResult execResult) {
        lock.lock();
        try {
            try {
                execResult.rethrowFailure().assertNormalExitValue();
            } catch (Throwable e) {
                processFailure = e;
            }
            running = false;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return execHandle.toString();
    }

    public ObjectConnection getConnection() {
        return connection;
    }

    public void start() {
        lock.lock();
        try {
            running = true;
        } finally {
            lock.unlock();
        }

        execHandle.start();

        Date connectExpiry = new Date(System.currentTimeMillis() + connectTimeout);
        lock.lock();
        try {
            while (connection == null && running) {
                try {
                    if (!condition.awaitUntil(connectExpiry)) {
                        throw new ExecException(String.format("Timeout after waiting %.1f seconds for %s to connect.", ((double) connectTimeout) / 1000, execHandle));
                    }
                } catch (InterruptedException e) {
                    throw UncheckedException.throwAsUncheckedException(e);
                }
            }
            if (processFailure != null) {
                throw UncheckedException.throwAsUncheckedException(processFailure);
            }
            if (connection == null) {
                throw new ExecException(String.format("Never received a connection from %s.", execHandle));
            }
        } finally {
            lock.unlock();
        }
    }

    public ExecResult waitForStop() {
        ExecResult result = execHandle.waitForFinish();
        ObjectConnection connection;
        lock.lock();
        try {
            connection = this.connection;
        } finally {
            this.connection = null;
            this.execHandle = null;
            lock.unlock();
        }
        if (connection != null) {
            connection.stop();
        }
        return result.assertNormalExitValue();
    }
}
