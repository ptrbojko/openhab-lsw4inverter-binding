/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
 * <p>
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.lswlogger.internal.protocolv5.sn23x;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.lswlogger.internal.LoggerThingConfiguration;
import org.openhab.binding.lswlogger.internal.protocolv5.AbstractLoggerHandler;
import org.openhab.binding.lswlogger.internal.protocolv5.ResponseDispatcher;
import org.openhab.binding.lswlogger.internal.protocolv5.UnknownResponseHandler;
import org.openhab.binding.lswlogger.internal.protocolv5.states.ConnectingState;
import org.openhab.binding.lswlogger.internal.protocolv5.states.ProtocolState;
import org.openhab.binding.lswlogger.internal.protocolv5.states.ReadingResponseState;
import org.openhab.binding.lswlogger.internal.protocolv5.states.ReconnectingState;
import org.openhab.binding.lswlogger.internal.protocolv5.states.SendingRequestState;
import org.openhab.binding.lswlogger.internal.protocolv5.states.StateBuilder;
import org.openhab.binding.lswlogger.internal.protocolv5.states.StateMachine;
import org.openhab.binding.lswlogger.internal.protocolv5.states.UnrecoverableErrorState;
import org.openhab.binding.lswlogger.internal.protocolv5.states.WaitingForNextSendRequestState;
import org.openhab.binding.lswlogger.internal.protocolv5.states.WaitingToWakeupInverterReconnectingState;
import org.openhab.core.thing.Thing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SN23xLoggerHandler} is responsible for handling commands, which
 * are
 * sent to one of the channels.
 *
 * @author Piotr Bojko - Initial contribution
 */

public class SN23xLoggerHandler extends AbstractLoggerHandler {

    private static final Logger logger = LoggerFactory.getLogger(SN23xLoggerHandler.class);

    private static final int GROUP_1_FROM_REGISTER = 0x0404;
    private static final int GROUP_1_TO_REGISTER = 0x042E;

    private static final int GROUP_2_FROM_REGISTER = 0x0484;
    private static final int GROUP_2_TO_REGISTER = 0x04AF;

    public SN23xLoggerHandler(Thing thing) {
        super(thing);
    }

    @Override
    protected StateMachine<?> createStateMachine() {
        return createStateMachine(new SN23xLoggerHandlerContext(),
                this.getConfigAs(LoggerThingConfiguration.class));
    }

    private StateMachine<SN23xLoggerHandlerContext> createStateMachine(
            @NonNull SN23xLoggerHandlerContext lswLoggerHandlerContext,
            @NonNull LoggerThingConfiguration configAs) {
        ProtocolState<SN23xLoggerHandlerContext> initial = new ConnectingState<>();
        UnrecoverableErrorState<SN23xLoggerHandlerContext> unrecoverableErrorState = new UnrecoverableErrorState<>();
        SendingRequestState<SN23xLoggerHandlerContext> sendingRequestForGroup1State = new SendingRequestState<>(
                GROUP_1_FROM_REGISTER,
                GROUP_1_TO_REGISTER);
        SendingRequestState<SN23xLoggerHandlerContext> sendingRequestForGroup2State = new SendingRequestState<>(
                GROUP_2_FROM_REGISTER,
                GROUP_2_TO_REGISTER);
        ReadingResponseState<SN23xLoggerHandlerContext> readingResponse1State = new ReadingResponseState<>(
                createResponseDispatcher());
        ReadingResponseState<SN23xLoggerHandlerContext> readingResponse2State = new ReadingResponseState<>(
                createResponseDispatcher());
        WaitingForNextSendRequestState<SN23xLoggerHandlerContext> cycleWaiting1State = new WaitingForNextSendRequestState<>();
        WaitingForNextSendRequestState<SN23xLoggerHandlerContext> cycleWaiting2State = new WaitingForNextSendRequestState<>();
        ReconnectingState<SN23xLoggerHandlerContext> reconnectingState = new ReconnectingState<>();
        WaitingToWakeupInverterReconnectingState<SN23xLoggerHandlerContext> waitingToWakeupInverterReconnectingState = new WaitingToWakeupInverterReconnectingState<>();
        StateBuilder<SN23xLoggerHandlerContext> builder = new StateBuilder<>();
        builder
                .addContext(lswLoggerHandlerContext)
                .addConfiguration(configAs)
                .setInitial(initial)
                .addState(initial,
                        routes -> routes.addNextRoute(sendingRequestForGroup1State)
                                .addErrorRoute(unrecoverableErrorState))
                .addState(sendingRequestForGroup1State,
                        routes -> routes.addNextRoute(readingResponse1State)
                                .addExceptionRoute(reconnectingState))
                .addState(readingResponse1State,
                        routes -> routes.addNextRoute(cycleWaiting1State)
                                .addExceptionRoute(reconnectingState))
                .addState(reconnectingState,
                        routes -> routes.addNextRoute(sendingRequestForGroup1State)
                                .addAlternativeRoute(
                                        waitingToWakeupInverterReconnectingState)
                                .addExceptionRoute(reconnectingState))
                .addState(cycleWaiting1State, routes -> routes.addNextRoute(sendingRequestForGroup2State))
                .addState(sendingRequestForGroup2State,
                        routes -> routes.addNextRoute(readingResponse2State)
                                .addExceptionRoute(reconnectingState))
                .addState(readingResponse2State,
                        routes -> routes.addNextRoute(cycleWaiting2State)
                                .addExceptionRoute(reconnectingState))
                .addState(cycleWaiting2State, routes -> routes.addNextRoute(sendingRequestForGroup1State))
                .addState(waitingToWakeupInverterReconnectingState,
                        route -> route.addNextRoute(sendingRequestForGroup1State)
                                .addExceptionRoute(
                                        waitingToWakeupInverterReconnectingState)
                                .addErrorRoute(unrecoverableErrorState));
        return builder.build();
    }

    private ResponseDispatcher createResponseDispatcher() {
        return ResponseDispatcher.create(
                new UnknownResponseHandler());
    }

    private class SN23xLoggerHandlerContext extends AbstractLoggerHandler.AbstractContext {

        public SN23xLoggerHandlerContext() {
            super(SN23xLoggerHandler.this);
        }

        @Override
        public void notifyLoggerIsOffline() {
            super.notifyLoggerIsOffline();
        }
    }
}
