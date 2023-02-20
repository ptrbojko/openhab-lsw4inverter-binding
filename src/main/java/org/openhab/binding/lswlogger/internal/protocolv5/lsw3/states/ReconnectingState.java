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
package org.openhab.binding.lswlogger.internal.protocolv5.lsw3.states;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

import org.openhab.binding.lswlogger.internal.LoggerThingConfiguration;
import org.openhab.binding.lswlogger.internal.connection.Context;
import org.openhab.binding.lswlogger.internal.connection.StateMachineSwitchable;
import org.openhab.binding.lswlogger.internal.protocolv5.states.ProtocolState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReconnectingState<C extends Context> implements ProtocolState<C> {

    private static final Logger logger = LoggerFactory.getLogger(ReconnectingState.class);
    public static final int WAIT_BEFORE_RECONNECT_SECONDS = 20;

    private final RetriesCounter retriesCounter = new RetriesCounter();

    @Override
    public void tick(StateMachineSwitchable sm, C context, LoggerThingConfiguration configuration) {
        if (retriesCounter.getRetries() > configuration.getRetriesCount()) {
            sm.switchToAlternativeState();
            return;
        }
        close(context, configuration);
        reopenChannel(context);
        context.schedule(WAIT_BEFORE_RECONNECT_SECONDS, TimeUnit.SECONDS,
                () -> context.channel().connect(
                        new InetSocketAddress(configuration.getHostname(), configuration.getPort()), null,
                        new ReconnectingHandler(sm)));
    }

    private void reopenChannel(Context context) {
        try {
            context.openChannel();
        } catch (IOException e) {
            logger.warn("Problem reopening channel", e);
        }
    }

    @Override
    public void close(C context, LoggerThingConfiguration configuration) {
        try {
            context.channel().close();
        } catch (IOException e) {
            logger.error("Cannot disconnect from channel", e);
        }
    }

    private class ReconnectingHandler implements CompletionHandler<Void, Void> {

        private final StateMachineSwitchable sm;

        public ReconnectingHandler(StateMachineSwitchable sm) {
            this.sm = sm;
        }

        @Override
        public void completed(Void unused, Void unused2) {
            retriesCounter.clearRetries();
            sm.switchToNextState();
        }

        @Override
        public void failed(Throwable t, Void unused) {
            logger.error("Error connecting", t);
            retriesCounter.increaseRetries();
            sm.switchToExceptionState();
        }
    }

    private static class RetriesCounter {

        private int counter = 0;

        int getRetries() {
            return counter;
        };

        void increaseRetries() {
            counter++;
        }

        void clearRetries() {
            counter = 0;
        }
    }
}
