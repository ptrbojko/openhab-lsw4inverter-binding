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
import org.openhab.binding.lswlogger.internal.protocolv5.states.StateMachine;
import org.openhab.binding.lswlogger.internal.protocolv5.states.StateMachineBuilder;
import org.openhab.binding.lswlogger.internal.protocolv5.states.UnrecoverableErrorState;
import org.openhab.binding.lswlogger.internal.protocolv5.states.WaitingToWakeupInverterReconnectingState;
import org.openhab.core.thing.Thing;

/**
 * The {@link SN23xLoggerHandler} is responsible for handling commands, which
 * are
 * sent to one of the channels.
 *
 * @author Piotr Bojko - Initial contribution
 */

public class SN23xLoggerHandler extends AbstractLoggerHandler {

    private static final int SYSTEM_INFO_FROM_REG = 0x0404;
    private static final int SYSTEM_INFO_TO_REG = 0x042E;

    private static final int GRID_INFO_FROM_REG = 0x0480;
    private static final int GRID_INFO_TO_REG = 0x04BC;

    private static final int ENERGY_TOTALS_FROM_REG = 0x0680;
    private static final int ENERGY_TOTALS_TO_REG = 0x049B;

    public SN23xLoggerHandler(Thing thing) {
        super(thing);
    }

    @Override
    protected StateMachine<?, ?> createStateMachine() {
        return createStateMachine(new SN23xLoggerHandlerContext(),
                this.getConfigAs(LoggerThingConfiguration.class));
    }

    private StateMachine<LoggerThingConfiguration, SN23xLoggerHandlerContext> createStateMachine(
            @NonNull SN23xLoggerHandlerContext lswLoggerHandlerContext,
            @NonNull LoggerThingConfiguration configAs) {
        ProtocolState<LoggerThingConfiguration, SN23xLoggerHandlerContext> initial = new ConnectingState<>();
        UnrecoverableErrorState<SN23xLoggerHandlerContext> unrecoverableErrorState = new UnrecoverableErrorState<>();
        SendingRequestState<SN23xLoggerHandlerContext> sendingSystemInfoRequestState = new SendingRequestState<>(
                SYSTEM_INFO_FROM_REG,
                SYSTEM_INFO_TO_REG);
        ReadingResponseState<SN23xLoggerHandlerContext> readingSystemInfoState = new ReadingResponseState<>(
                createResponseDispatcherForSystemInfo());
        SendingRequestState<SN23xLoggerHandlerContext> sendingGridInfoRequestState = new SendingRequestState<>(
                GRID_INFO_FROM_REG,
                GRID_INFO_TO_REG);
        ReadingResponseState<SN23xLoggerHandlerContext> readingGridInfoState = new ReadingResponseState<>(
                createResponseDispatcherForGridInfo());
        SendingRequestState<SN23xLoggerHandlerContext> sendingEnergyTotalsRequestState = new SendingRequestState<>(
                ENERGY_TOTALS_FROM_REG,
                ENERGY_TOTALS_TO_REG);
        ReadingResponseState<SN23xLoggerHandlerContext> readingEnergyTotalsState = new ReadingResponseState<>(
                createResponseDispatcherForEnergyTotal());
        ReconnectingState<SN23xLoggerHandlerContext> reconnectingState = new ReconnectingState<>();
        WaitingToWakeupInverterReconnectingState<SN23xLoggerHandlerContext> waitingToWakeupInverterReconnectingState = new WaitingToWakeupInverterReconnectingState<>();
        StateMachineBuilder<LoggerThingConfiguration, SN23xLoggerHandlerContext> builder = new StateMachineBuilder<>();
        builder
                .addContext(lswLoggerHandlerContext)
                .addConfiguration(configAs)
                .addScheduler(scheduler)
                .setInitial(initial)
                .addState("Initial", initial,
                        routes -> routes.addNextRoute(sendingSystemInfoRequestState)
                                .addExceptionRoute(reconnectingState)
                                .addErrorRoute(unrecoverableErrorState))
                .addState("Sending system info request", sendingSystemInfoRequestState,
                        routes -> routes.addNextRoute(readingSystemInfoState)
                                .addExceptionRoute(reconnectingState))
                .addState("Reading system info response", readingSystemInfoState,
                        routes -> routes.addNextRoute(sendingGridInfoRequestState)
                                .addExceptionRoute(reconnectingState))
                .addState("Sending grid info request", sendingGridInfoRequestState,
                        routes -> routes.addNextRoute(readingGridInfoState)
                                .addExceptionRoute(reconnectingState))
                .addState("Reaidng grid info response", readingGridInfoState,
                        routes -> routes.addNextRoute(sendingEnergyTotalsRequestState)
                                .addExceptionRoute(reconnectingState))
                .addState("Sending energy totals request", sendingEnergyTotalsRequestState,
                        routes -> routes.addNextRoute(readingEnergyTotalsState)
                                .addExceptionRoute(reconnectingState))
                .addState("Reading energy totals response", readingEnergyTotalsState,
                        routes -> routes.addNextRoute(sendingSystemInfoRequestState)
                                .addExceptionRoute(reconnectingState))
                .addState("Reconnecting", reconnectingState,
                        routes -> routes.addNextRoute(sendingSystemInfoRequestState)
                                .addAlternativeRoute(
                                        waitingToWakeupInverterReconnectingState)
                                .addExceptionRoute(reconnectingState))
                .addState("Watining for inverter to wakeup", waitingToWakeupInverterReconnectingState,
                        route -> route.addNextRoute(sendingSystemInfoRequestState)
                                .addExceptionRoute(
                                        waitingToWakeupInverterReconnectingState)
                                .addErrorRoute(unrecoverableErrorState));
        return builder.build();
    }

    private ResponseDispatcher createResponseDispatcherForEnergyTotal() {
        return ResponseDispatcher.create(
                new From0680To069bDataResponseHandler(this::updateState),
                new UnknownResponseHandler());
    }

    private ResponseDispatcher createResponseDispatcherForGridInfo() {
        return ResponseDispatcher.create(
                new From0480To04afDataResponseHandler(this::updateState),
                new UnknownResponseHandler());
    }

    private ResponseDispatcher createResponseDispatcherForSystemInfo() {
        return ResponseDispatcher.create(
                new From0404To042eDataResponseHandler(this::updateState),
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
