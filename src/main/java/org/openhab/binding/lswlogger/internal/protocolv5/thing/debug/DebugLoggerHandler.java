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
package org.openhab.binding.lswlogger.internal.protocolv5.thing.debug;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.lswlogger.internal.LoggerThingConfiguration;
import org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.DebugLoggerV5;
import org.openhab.binding.lswlogger.internal.protocolv5.AbstractLoggerHandler;
import org.openhab.binding.lswlogger.internal.protocolv5.ResponseDispatcher;
import org.openhab.binding.lswlogger.internal.protocolv5.states.ConnectingState;
import org.openhab.binding.lswlogger.internal.protocolv5.states.ProtocolState;
import org.openhab.binding.lswlogger.internal.protocolv5.states.ReadingResponseState;
import org.openhab.binding.lswlogger.internal.protocolv5.states.ReconnectingState;
import org.openhab.binding.lswlogger.internal.protocolv5.states.StateMachine;
import org.openhab.binding.lswlogger.internal.protocolv5.states.StateMachineBuilder;
import org.openhab.binding.lswlogger.internal.protocolv5.states.UnrecoverableErrorState;
import org.openhab.binding.lswlogger.internal.protocolv5.states.WaitingToWakeupInverterReconnectingState;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.Thing;

/**
 * The {@link DebugLoggerHandler} is responsible for handling commands, which
 * are
 * sent to one of the channels.
 *
 * @author Piotr Bojko - Initial contribution
 */

public class DebugLoggerHandler extends AbstractLoggerHandler {

    public DebugLoggerHandler(Thing thing) {
        super(thing);
    }

    @Override
    protected StateMachine<?, ?> createStateMachine() {
        return createStateMachine(
                new DebugLoggerHandlerContext(),
                this.getConfigAs(DebugLoggerThingConfiguration.class));
    }

    private StateMachine<DebugLoggerThingConfiguration, DebugLoggerHandlerContext> createStateMachine(
            @NonNull DebugLoggerHandlerContext lswLoggerHandlerContext,
            @NonNull LoggerThingConfiguration configAs) {
        ProtocolState<DebugLoggerThingConfiguration, DebugLoggerHandlerContext> connecting = new ConnectingState<>();
        UnrecoverableErrorState<DebugLoggerThingConfiguration, DebugLoggerHandlerContext> unrecoverableErrorState = new UnrecoverableErrorState<>();
        ConfigurableSendingRequestState<DebugLoggerHandlerContext> sendingRequestState = new ConfigurableSendingRequestState<>();
        ReadingResponseState<DebugLoggerThingConfiguration, DebugLoggerHandlerContext> readingResponseState = new ReadingResponseState<>(
                createResponseDispatcher());
        ReconnectingState<DebugLoggerThingConfiguration, DebugLoggerHandlerContext> reconnectingState = new ReconnectingState<>();
        WaitingToWakeupInverterReconnectingState<DebugLoggerThingConfiguration, DebugLoggerHandlerContext> waitingToWakeupInverterReconnectingState = new WaitingToWakeupInverterReconnectingState<>();
        StateMachineBuilder<DebugLoggerThingConfiguration, DebugLoggerHandlerContext> builder = new StateMachineBuilder<>();
        builder
                .addContext(lswLoggerHandlerContext)
                .addConfiguration(configAs)
                .addScheduler(scheduler)
                .setInitial(connecting)
                .addState("Initial", connecting,
                        routes -> routes.addNextRoute(sendingRequestState)
                                .addExceptionRoute(reconnectingState)
                                .addErrorRoute(unrecoverableErrorState))
                .addState("Sending request", sendingRequestState,
                        routes -> routes.addNextRoute(readingResponseState)
                                .addExceptionRoute(reconnectingState))
                .addState("Reading response", readingResponseState,
                        routes -> routes.addNextRoute(sendingRequestState)
                                .addExceptionRoute(reconnectingState))
                .addState("Reconnecting", reconnectingState,
                        routes -> routes.addNextRoute(sendingRequestState)
                                .addAlternativeRoute(
                                        waitingToWakeupInverterReconnectingState)
                                .addExceptionRoute(reconnectingState))
                .addState("Watining for inverter to wakeup", waitingToWakeupInverterReconnectingState,
                        route -> route.addNextRoute(sendingRequestState)
                                .addExceptionRoute(
                                        waitingToWakeupInverterReconnectingState)
                                .addErrorRoute(unrecoverableErrorState));
        return builder.build();
    }

    private ResponseDispatcher createResponseDispatcher() {
        return ResponseDispatcher.create(
                new DebugDataResponseHandler(this::updateState));
    }

    private class DebugLoggerHandlerContext
            extends AbstractLoggerHandler.AbstractContext<DebugLoggerThingConfiguration> {

        public DebugLoggerHandlerContext() {
            super(DebugLoggerHandler.this);
        }

        @Override
        public void notifyLoggerIsOffline() {
            super.notifyLoggerIsOffline();
            updateState(DebugLoggerV5.lastResponseChannel, StringType.valueOf(""));
        }

        @Override
        protected @NonNull Class<DebugLoggerThingConfiguration> getConfigClass() {
            return DebugLoggerThingConfiguration.class;
        }
    }
}
