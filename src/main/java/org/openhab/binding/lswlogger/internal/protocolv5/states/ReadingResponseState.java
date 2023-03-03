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
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.channels.InterruptedByTimeoutException;
import java.time.Duration;
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

public class ReadingResponseState<C extends Context> implements ProtocolState<C> {

    private static final Logger logger = LoggerFactory.getLogger(ReadingResponseState.class);

    private final ResponseDispatcher responseDispatcher;
    private final ByteBuffer response = ByteBuffer.allocate(1024);

    public ReadingResponseState(ResponseDispatcher responseDispatcher) {
        this.responseDispatcher = responseDispatcher;
    }

    @Override
    public void tick(StateMachineSwitchable sm, C context, LoggerThingConfiguration configuration) {
        response.clear();
        context.channel().read(response, configuration.getRefreshTime(), TimeUnit.SECONDS, null,
                new ReadingHandler(sm, context, response, configuration.getRefreshTime()));
    }

    @Override
    public void close(C context, LoggerThingConfiguration configuration) {
        try {
            context.channel().close();
        } catch (IOException e) {
            logger.error("Cannot disconnect from channel", e);
        }
    }

    private class ReadingHandler implements CompletionHandler<Integer, Void> {

        private final ByteBuffer response;
        private final Instant enterTime;
        private final StateMachineSwitchable sm;
        private final Context context;
        private final long period;

        public ReadingHandler(StateMachineSwitchable sm, Context context, ByteBuffer response, long period) {
            this.period = period;
            this.enterTime = Instant.now();
            this.sm = sm;
            this.context = context;
            this.response = response;
        }

        @Override
        public void completed(Integer integer, Void unused) {
            context.updateState(Common.onlineChannel, OnOffType.ON);
            context.updateStatus(ThingStatus.ONLINE);
            response.flip();
            responseDispatcher.accept(response);
            Instant now = Instant.now();
            if (enterTime.plusSeconds(period).isBefore(Instant.now())) {
                sm.switchToNextState();
            } else {
                response.clear();
                long pastPeriod = Duration.between(enterTime, now).toSeconds();
                context.channel().read(response, Math.max(period - pastPeriod, 0), TimeUnit.SECONDS, null, this);
            }
        }

        @Override
        public void failed(Throwable throwable, Void unused) {
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
