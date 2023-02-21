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

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.types.State;

public interface Context {

    void schedule(int i, TimeUnit seconds, Runnable runnable);

    void openChannel() throws IOException;

    AsynchronousSocketChannel channel();

    void notifyLoggerIsOffline();

    void notifyCannotRecover();

    void updateState(@NonNull String uuid, @NonNull State state);

    void updateStatus(@NonNull ThingStatus online);

}
