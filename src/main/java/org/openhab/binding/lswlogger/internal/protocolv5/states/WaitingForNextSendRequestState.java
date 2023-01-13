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
package org.openhab.binding.lswlogger.internal.protocolv5.states;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.TimeUnit;

import org.openhab.binding.lswlogger.internal.LoggerThingConfiguration;
import org.openhab.binding.lswlogger.internal.connection.Context;
import org.openhab.binding.lswlogger.internal.connection.LoggerConnectionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaitingForNextSendRequestState implements LoggerConnectionState {

    private static final Logger logger = LoggerFactory.getLogger(WaitingForNextSendRequestState.class);

    private final AsynchronousSocketChannel channel;

    public WaitingForNextSendRequestState(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void tick(Context context, LoggerThingConfiguration configuration) {
        context.schedule(configuration.getRefreshTime(), TimeUnit.SECONDS,
                () -> context.switchTo(new SendingRequestState(channel)));
    }

    @Override
    public void close() {
        try {
            channel.close();
        } catch (IOException e) {
            logger.error("Cannot disconnect from channel", e);
        }
    }
}
