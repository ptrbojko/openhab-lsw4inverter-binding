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

import java.nio.ByteBuffer;
import java.nio.channels.InterruptedByTimeoutException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.openhab.binding.lswlogger.internal.LoggerThingConfiguration;
import org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.Common;
import org.openhab.binding.lswlogger.internal.connection.Context;
import org.openhab.binding.lswlogger.internal.connection.StateMachineSwitchable;
import org.openhab.binding.lswlogger.internal.protocolv5.ResponseDispatcher;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.ThingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadingResponseState<L extends LoggerThingConfiguration, C extends Context<L>>
        implements ProtocolState<L, C> {

    private static final Logger logger = LoggerFactory.getLogger(ReadingResponseState.class);
    private static final int WAIT_FOR_NEXT_READ = 15;

    private final ResponseDispatcher responseDispatcher;

    public ReadingResponseState(ResponseDispatcher responseDispatcher) {
        this.responseDispatcher = responseDispatcher;
    }

    @Override
    public void handle(StateMachineSwitchable sm, C context) {
        new ReadingHandler(sm, context, context.config().getRefreshTime()).run();
    }

    private class ReadingHandler {

        private final Instant enterTime;
        private final StateMachineSwitchable sm;
        private final C context;
        private final long period;

        public ReadingHandler(StateMachineSwitchable sm, C context, long period) {
            this.period = period;
            this.enterTime = Instant.now();
            this.sm = sm;
            this.context = context;
        }

        public void run() {
            context.channel().read(this::onRead, this::handleNoMessage, this::failed);
        }

        public void onRead(ByteBuffer message) {
            logger.debug("Trying to read a message");
            context.updateState(Common.onlineChannel, OnOffType.ON);
            context.updateStatus(ThingStatus.ONLINE);
            responseDispatcher.accept(message);
        }

        public void handleNoMessage() {
            logger.debug("No message to read, trying to schedule next read or switching to next state");
            if (enterTime.plusSeconds(period).isBefore(Instant.now())) {
                sm.switchToNextState();
            } else {
                sm.schedule(WAIT_FOR_NEXT_READ, TimeUnit.SECONDS, this::run);
            }
        }

        public void failed(Throwable throwable) {
            if (throwable instanceof InterruptedByTimeoutException) {
                logger.debug("Got interrupted by timeout exception. This is ok - time to next state.");
                sm.switchToNextState();
            } else {
                logger.error("Problem reading response data", throwable);
                sm.switchToExceptionState();
            }
        }
    }
}
