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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import org.openhab.binding.lswlogger.internal.LoggerThingConfiguration;
import org.openhab.binding.lswlogger.internal.connection.Context;
import org.openhab.binding.lswlogger.internal.connection.StateMachineSwitchable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaitingToWakeupInverterReconnectingState<C extends Context> implements ProtocolState<C> {

    private static final Logger logger = LoggerFactory.getLogger(WaitingToWakeupInverterReconnectingState.class);

    private final EnterWaitPeriodGate enterGate = new EnterWaitPeriodGate();

    @Override
    public void tick(StateMachineSwitchable sm, C context, LoggerThingConfiguration configuration) {
        if (enterGate.enterWaitPeriod().plus(configuration.getMaxOfflineTime(), ChronoUnit.MINUTES)
                .isBefore(Instant.now())) {
            sm.switchToErrorState();
            return;
        }
        context.notifyLoggerIsOffline();
        close(context, configuration);
        context.schedule(calculateWaitPeriod(configuration), TimeUnit.MINUTES, () -> context.channel().reopen(
                new InetSocketAddress(configuration.getHostname(), configuration.getPort()),
                () -> {
                    enterGate.leaveWaitPeriod();
                    sm.switchToNextState();
                },
                t -> {
                    logger.error("Error opening", t);
                    sm.switchToExceptionState();
                },
                t -> {
                    logger.error("Error connecting", t);
                    sm.switchToExceptionState();
                }));
    }

    private int calculateWaitPeriod(LoggerThingConfiguration configuration) {
        return (int) Math.sqrt(configuration.getMaxOfflineTime());
    }

    @Override
    public void close(C context, LoggerThingConfiguration configuration) {
        try {
            context.channel().close();
        } catch (IOException e) {
            logger.error("Cannot disconnect from channel", e);
        }
    }

    private static class EnterWaitPeriodGate {

        private Instant enterTime;

        public Instant enterWaitPeriod() {
            if (enterTime == null) {
                enterTime = Instant.now();
            }
            return enterTime;
        };

        public void leaveWaitPeriod() {
            enterTime = null;
        };
    }
}
