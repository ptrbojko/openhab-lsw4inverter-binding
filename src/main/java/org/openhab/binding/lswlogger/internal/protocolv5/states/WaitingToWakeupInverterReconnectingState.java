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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import org.openhab.binding.lswlogger.internal.LoggerThingConfiguration;
import org.openhab.binding.lswlogger.internal.connection.Context;
import org.openhab.binding.lswlogger.internal.connection.LoggerConnectionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaitingToWakeupInverterReconnectingState implements LoggerConnectionState {

    private static final Logger logger = LoggerFactory.getLogger(WaitingToWakeupInverterReconnectingState.class);

    private final Instant enterTimestamp;
    private AsynchronousSocketChannel channel;

    public WaitingToWakeupInverterReconnectingState(AsynchronousSocketChannel channel) {
        this(channel, Instant.now());
    }

    public WaitingToWakeupInverterReconnectingState(AsynchronousSocketChannel channel, Instant enterTimestamp) {
        this.channel = channel;
        this.enterTimestamp = enterTimestamp;
    }

    @Override
    public void tick(Context context, LoggerThingConfiguration configuration) {
        if (enterTimestamp.plus(configuration.getMaxOfflineTime(), ChronoUnit.MINUTES).isBefore(Instant.now())) {
            context.switchTo(new UnrecoverableErrorState(this));
            return;
        }
        context.notifyLoggerIsOffline();
        close();
        reopenChannel();
        context.schedule(calculateWaitPeriod(configuration), TimeUnit.MINUTES, () -> {
            channel.connect(new InetSocketAddress(configuration.getHostname(), configuration.getPort()), null,
                    new ReconnectingHandler(context));
        });
    }

    private int calculateWaitPeriod(LoggerThingConfiguration configuration) {
        return (int) Math.sqrt(configuration.getMaxOfflineTime());
    }

    private void reopenChannel() {
        try {
            channel = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            logger.warn("Problem reopening channel", e);
        }
    }

    @Override
    public void close() {
        try {
            channel.close();
        } catch (IOException e) {
            logger.error("Cannot disconnect from channel", e);
        }
    }

    private class ReconnectingHandler implements CompletionHandler<Void, Void> {

        private final Context context;

        public ReconnectingHandler(Context context) {
            this.context = context;
        }

        @Override
        public void completed(Void unused, Void unused2) {
            context.switchTo(new SendingRequestState(channel));
        }

        @Override
        public void failed(Throwable t, Void unused) {
            logger.error("Error connecting", t);
            context.switchTo(new WaitingToWakeupInverterReconnectingState(channel, enterTimestamp));
        }
    }
}
