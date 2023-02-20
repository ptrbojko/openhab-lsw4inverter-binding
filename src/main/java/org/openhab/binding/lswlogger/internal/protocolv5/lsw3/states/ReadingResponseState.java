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
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

import org.openhab.binding.lswlogger.internal.LoggerThingConfiguration;
import org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.Common;
import org.openhab.binding.lswlogger.internal.connection.Context;
import org.openhab.binding.lswlogger.internal.connection.StateMachineSwitchable;
import org.openhab.binding.lswlogger.internal.protocolv5.ResponseDispatcher;
import org.openhab.binding.lswlogger.internal.protocolv5.states.ProtocolState;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.ThingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadingResponseState<C extends Context> implements ProtocolState<C> {

    private static final Logger logger = LoggerFactory.getLogger(ReadingResponseState.class);

    private final ResponseDispatcher responseDispatcher;

    public ReadingResponseState(ResponseDispatcher responseDispatcher) {
        this.responseDispatcher = responseDispatcher;
    }

    @Override
    public void tick(StateMachineSwitchable sm, C context, LoggerThingConfiguration configuration) {
        ByteBuffer response = context.response();
        response.clear();
        context.channel().read(response, null, new ReadingHandler(sm, context, response));
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
        private StateMachineSwitchable sm;
        private Context context;

        public ReadingHandler(StateMachineSwitchable sm, Context context, ByteBuffer response) {
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
            sm.switchToNextState();
        }

        @Override
        public void failed(Throwable throwable, Void unused) {
            sm.switchToExceptionState();
        }
    }
}
