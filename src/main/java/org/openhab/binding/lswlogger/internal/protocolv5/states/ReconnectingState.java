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
import java.util.concurrent.TimeUnit;

import org.openhab.binding.lswlogger.internal.LoggerThingConfiguration;
import org.openhab.binding.lswlogger.internal.connection.Context;
import org.openhab.binding.lswlogger.internal.connection.StateMachineSwitchable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReconnectingState<L extends LoggerThingConfiguration, C extends Context<L>>
        implements ProtocolState<L, C> {

    private static final Logger logger = LoggerFactory.getLogger(ReconnectingState.class);
    public static final int WAIT_BEFORE_RECONNECT_SECONDS = 20;

    private final RetriesCounter retriesCounter = new RetriesCounter();

    @Override
    public void handle(StateMachineSwitchable sm, C context) {
        if (retriesCounter.getRetries() > context.config().getRetriesCount()) {
            retriesCounter.clearRetries();
            sm.switchToAlternativeState();
            return;
        }
        logger.debug("Retrying to connect. Tries count {}", retriesCounter.getRetries());
        close(context);
        sm.schedule(WAIT_BEFORE_RECONNECT_SECONDS, TimeUnit.SECONDS,
                () -> context.channel().reopen(
                        new InetSocketAddress(context.config().getHostname(), context.config().getPort()),
                        () -> {
                            retriesCounter.clearRetries();
                            sm.switchToNextState();
                        },
                        t -> {
                            logger.error("Error opening", t);
                            retriesCounter.increaseRetries();
                            sm.switchToExceptionState();
                        },
                        t -> {
                            logger.error("Error connecting", t);
                            retriesCounter.increaseRetries();
                            sm.switchToExceptionState();
                        }));
    }

    private void close(C context) {
        try {
            context.channel().close();
        } catch (IOException e) {
            logger.error("Cannot disconnect from channel", e);
        }
    }

    private static class RetriesCounter {

        private int counter = 1;

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
