/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.lswlogger.internal.connection;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public interface Context {
    void switchTo(LoggerConnectionState state);

    ByteBuffer response();

    ByteBuffer request();

    void handleResponse();

    void schedule(int i, TimeUnit seconds, Runnable runnable);

    void notifyLoggerIsOffline();

    void notifyCannotRecover();
}
