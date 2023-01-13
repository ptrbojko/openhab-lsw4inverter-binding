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
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.openhab.binding.lswlogger.internal.LoggerThingConfiguration;
import org.openhab.binding.lswlogger.internal.connection.Context;
import org.openhab.binding.lswlogger.internal.connection.LoggerConnectionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitialState implements LoggerConnectionState {

    private static final Logger logger = LoggerFactory.getLogger(InitialState.class);
    private AsynchronousSocketChannel channel;

    @Override
    public void tick(Context context, LoggerThingConfiguration configuration) {
        try {
            this.channel = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            logger.error("Cannot open nio channel", e);
            context.switchTo(new UnrecoverableErrorState(this));
            return;
        }
        channel.connect(new InetSocketAddress(configuration.getHostname(), configuration.getPort()), null,
                new ConnectingHandler(context));
    }

    @Override
    public void close() {
        try {
            channel.close();
        } catch (IOException e) {
            logger.error("Cannot disconnect from channel", e);
        }
    }

    private class ConnectingHandler implements CompletionHandler<Void, Void> {

        private final Context context;

        public ConnectingHandler(Context context) {
            this.context = context;
        }

        @Override
        public void completed(Void unused, Void unused2) {
            context.switchTo(new SendingRequestState(channel));
        }

        @Override
        public void failed(Throwable t, Void unused) {
            logger.error("Error connecting", t);
            context.switchTo(new ReconnectingState(channel, 0));
        }
    }
}
