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
package org.openhab.binding.lswlogger.internal.protocolv5.lsw3;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.lswlogger.internal.LoggerThingConfiguration;
import org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants;
import org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.InitialDataValues;
import org.openhab.binding.lswlogger.internal.protocolv5.AbstractLoggerHandler;
import org.openhab.binding.lswlogger.internal.protocolv5.ResponseDispatcher;
import org.openhab.binding.lswlogger.internal.protocolv5.UnknownResponseHandler;
import org.openhab.binding.lswlogger.internal.protocolv5.lsw3.states.ConnectingState;
import org.openhab.binding.lswlogger.internal.protocolv5.lsw3.states.ReadingResponseState;
import org.openhab.binding.lswlogger.internal.protocolv5.lsw3.states.ReconnectingState;
import org.openhab.binding.lswlogger.internal.protocolv5.lsw3.states.SendingRequestState;
import org.openhab.binding.lswlogger.internal.protocolv5.lsw3.states.UnrecoverableErrorState;
import org.openhab.binding.lswlogger.internal.protocolv5.lsw3.states.WaitingForNextSendRequestState;
import org.openhab.binding.lswlogger.internal.protocolv5.lsw3.states.WaitingToWakeupInverterReconnectingState;
import org.openhab.binding.lswlogger.internal.protocolv5.states.ProtocolState;
import org.openhab.binding.lswlogger.internal.protocolv5.states.StateBuilder;
import org.openhab.binding.lswlogger.internal.protocolv5.states.StateMachine;
import org.openhab.core.thing.Thing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link LswLoggerHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Piotr Bojko - Initial contribution
 */

public class LswLoggerHandler extends AbstractLoggerHandler {

    private static final Logger logger = LoggerFactory.getLogger(LswLoggerHandler.class);

    private static final int FROM_REGISTER = 0x0000;
    private static final int TO_REGISTER = 0x0027;

    private StateMachine<LswLoggerHandlerContext> stateMachine;

    public LswLoggerHandler(Thing thing) {
        super(thing);
    }

    @Override
    protected StateMachine<?> createStateMachine() {
        return createStateMachine(new LswLoggerHandlerContext(),
                this.getConfigAs(LoggerThingConfiguration.class));
    }

    private StateMachine<LswLoggerHandlerContext> createStateMachine(
            @NonNull LswLoggerHandlerContext lswLoggerHandlerContext, @NonNull LoggerThingConfiguration configAs) {
        ProtocolState<LswLoggerHandlerContext> initial = new ConnectingState<>();
        UnrecoverableErrorState<LswLoggerHandlerContext> unrecoverableErrorState = new UnrecoverableErrorState<>();
        SendingRequestState<LswLoggerHandlerContext> sendingRequestState = new SendingRequestState<>(FROM_REGISTER,
                TO_REGISTER);
        ReadingResponseState<LswLoggerHandlerContext> readingResponseState = new ReadingResponseState<>(
                createResponseDispatcher());
        WaitingForNextSendRequestState<LswLoggerHandlerContext> cycleWaitingState = new WaitingForNextSendRequestState<>();
        ReconnectingState<LswLoggerHandlerContext> reconnectingState = new ReconnectingState<>();
        WaitingToWakeupInverterReconnectingState<LswLoggerHandlerContext> waitingToWakeupInverterReconnectingState = new WaitingToWakeupInverterReconnectingState<>();
        StateBuilder<LswLoggerHandlerContext> builder = new StateBuilder<>();
        builder
                .addContext(lswLoggerHandlerContext)
                .addConfiguration(configAs)
                .setInitial(initial)
                .addState(initial,
                        routes -> routes.addNextRoute(sendingRequestState).addErrorRoute(unrecoverableErrorState))
                .addState(sendingRequestState,
                        routes -> routes.addNextRoute(readingResponseState).addExceptionRoute(reconnectingState))
                .addState(readingResponseState,
                        routes -> routes.addNextRoute(cycleWaitingState).addExceptionRoute(reconnectingState))
                .addState(reconnectingState,
                        routes -> routes.addNextRoute(sendingRequestState)
                                .addAlternativeRoute(waitingToWakeupInverterReconnectingState)
                                .addExceptionRoute(reconnectingState))
                .addState(cycleWaitingState, routes -> routes.addNextRoute(sendingRequestState))
                .addState(waitingToWakeupInverterReconnectingState,
                        route -> route.addNextRoute(sendingRequestState)
                                .addExceptionRoute(waitingToWakeupInverterReconnectingState)
                                .addErrorRoute(unrecoverableErrorState));
        return builder.build();
    }

    private ResponseDispatcher createResponseDispatcher() {
        return ResponseDispatcher.create(
                new DataResponseHandler(this::updateState),
                new ExtendedDataResponseHandler(this::updateState),
                new UnknownResponseHandler());
    }

    private class LswLoggerHandlerContext extends AbstractLoggerHandler.AbstractContext {

        public LswLoggerHandlerContext() {
            super(LswLoggerHandler.this);
        }

        @Override
        public void notifyLoggerIsOffline() {
            super.notifyLoggerIsOffline();
            updateState(LswLoggerBindingConstants.LSWLoggerV5.gridAVoltageChannel, InitialDataValues.ZERO_VOLTS);
            updateState(LswLoggerBindingConstants.LSWLoggerV5.gridACurrentChannel, InitialDataValues.ZERO_AMPERES);
            updateState(LswLoggerBindingConstants.LSWLoggerV5.gridAPowerChannel, InitialDataValues.ZERO_WATTS);
            updateState(LswLoggerBindingConstants.LSWLoggerV5.gridBVoltageChannel, InitialDataValues.ZERO_VOLTS);
            updateState(LswLoggerBindingConstants.LSWLoggerV5.gridBCurrentChannel, InitialDataValues.ZERO_AMPERES);
            updateState(LswLoggerBindingConstants.LSWLoggerV5.gridBPowerChannel, InitialDataValues.ZERO_WATTS);
            updateState(LswLoggerBindingConstants.LSWLoggerV5.outputActivePowerChannel, InitialDataValues.ZERO_WATTS);
            updateState(LswLoggerBindingConstants.LSWLoggerV5.outputReactivePowerChannel, InitialDataValues.ZERO_WATTS);
            updateState(LswLoggerBindingConstants.LSWLoggerV5.Phase1CurrentChannel, InitialDataValues.ZERO_AMPERES);
            updateState(LswLoggerBindingConstants.LSWLoggerV5.Phase2CurrentChannel, InitialDataValues.ZERO_AMPERES);
            updateState(LswLoggerBindingConstants.LSWLoggerV5.Phase3CurrentChannel, InitialDataValues.ZERO_AMPERES);
        }
    }
}
