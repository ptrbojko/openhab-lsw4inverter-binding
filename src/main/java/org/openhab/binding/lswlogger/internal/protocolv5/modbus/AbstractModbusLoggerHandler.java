package org.openhab.binding.lswlogger.internal.protocolv5.modbus;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.openhab.binding.lswlogger.internal.LoggerThingConfiguration;
import org.openhab.binding.lswlogger.internal.protocolv5.AbstractLoggerHandler;
import org.openhab.binding.lswlogger.internal.protocolv5.ResponseDispatcher;
import org.openhab.binding.lswlogger.internal.protocolv5.modbus.ModbusRegisterDefinitionBuilder.ModbusRegistryDefnition;
import org.openhab.binding.lswlogger.internal.protocolv5.states.ConnectingState;
import org.openhab.binding.lswlogger.internal.protocolv5.states.ProtocolState;
import org.openhab.binding.lswlogger.internal.protocolv5.states.ReadingResponseState;
import org.openhab.binding.lswlogger.internal.protocolv5.states.ReconnectingState;
import org.openhab.binding.lswlogger.internal.protocolv5.states.SendingRequestState;
import org.openhab.binding.lswlogger.internal.protocolv5.states.StateMachine;
import org.openhab.binding.lswlogger.internal.protocolv5.states.StateMachineBuilder;
import org.openhab.binding.lswlogger.internal.protocolv5.states.UnrecoverableErrorState;
import org.openhab.binding.lswlogger.internal.protocolv5.states.WaitingToWakeupInverterReconnectingState;
import org.openhab.binding.lswlogger.internal.utils.StreamableWindow;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.binding.builder.ThingBuilder;

public abstract class AbstractModbusLoggerHandler extends AbstractLoggerHandler {

    private List<ModbusRegistryDefnition> definitions;

    public AbstractModbusLoggerHandler(Thing thing) {
        super(thing);
    }

    @Override
    protected final StateMachine<?, ?> createStateMachine() {
        ProtocolState<LoggerThingConfiguration, ModbusLoggerHandlerContext> initial = new ConnectingState<>();
        ProtocolState<LoggerThingConfiguration, ModbusLoggerHandlerContext> waitingToWakeupInverterReconnectingState = new WaitingToWakeupInverterReconnectingState<>();
        ReconnectingState<LoggerThingConfiguration, ModbusLoggerHandlerContext> reconnectingState = new ReconnectingState<>();
        ProtocolState<LoggerThingConfiguration, ModbusLoggerHandlerContext> unrecoverableErrorState = new UnrecoverableErrorState<>();
        StateMachineBuilder<LoggerThingConfiguration, ModbusLoggerHandlerContext> builder = new StateMachineBuilder<LoggerThingConfiguration, ModbusLoggerHandlerContext>()
                .addContext(new ModbusLoggerHandlerContext())
                .addConfiguration(getConfigAs(LoggerThingConfiguration.class))
                .addScheduler(scheduler)
                .setInitial(initial);
        AtomicReference<SendAndReciveStates> firstSARStates = new AtomicReference<>(null);
        new StreamableWindow<>(definitions.stream().map(this::toSendAndReciveStates))
                .forEach((current, next) -> {
                    if (current == null) {
                        builder.addState("Initial", initial,
                                routes -> routes.addNextRoute(next.getSendState())
                                        .addExceptionRoute(reconnectingState)
                                        .addErrorRoute(unrecoverableErrorState));
                        firstSARStates.set(next);
                        return;
                    }
                    builder.addState("Sending request", current.getSendState(),
                            routes -> routes.addNextRoute(current.getResponseState())
                                    .addExceptionRoute(reconnectingState));
                    if (next == null) {
                        builder.addState("Receiving ", current.getResponseState(),
                                routes -> routes.addNextRoute(firstSARStates.get().getSendState())
                                        .addExceptionRoute(reconnectingState));
                        return;
                    }
                    builder.addState("Receiving ", current.getResponseState(),
                            routes -> routes.addNextRoute(next.getSendState())
                                    .addExceptionRoute(reconnectingState));
                });
        builder.addState("Reconnecting", reconnectingState,
                routes -> routes.addNextRoute(firstSARStates.get().getResponseState())
                        .addAlternativeRoute(
                                waitingToWakeupInverterReconnectingState)
                        .addExceptionRoute(reconnectingState))
                .addState("Watining for inverter to wakeup", waitingToWakeupInverterReconnectingState,
                        route -> route.addNextRoute(firstSARStates.get().sendState)
                                .addExceptionRoute(
                                        waitingToWakeupInverterReconnectingState)
                                .addErrorRoute(unrecoverableErrorState));
        return builder.build();
    }

    @Override
    protected void preInit() {
        this.definitions = createDefinitions();
        configureChannels();
    }

    private void configureChannels() {
        Thing thing = getThing();
        ThingBuilder thingBuilder = editThing();
        thingBuilder.withoutChannels(thing.getChannels().stream()
                .filter(c -> c.getProperties().containsKey(DynamicChannels.DYNAMIC))
                .toList());
        definitions.stream()
                .map(ModbusRegisterDefinitionBuilder.ModbusRegistryDefnition::getChannelConfigurer)
                .forEach(configurer -> configurer.configure(thing, thingBuilder));
        updateThing(thingBuilder.build());
    }

    protected abstract List<ModbusRegisterDefinitionBuilder.ModbusRegistryDefnition> createDefinitions();

    // todo: handle zeroing channels when offline
    private class ModbusLoggerHandlerContext extends AbstractLoggerHandler.AbstractContext<LoggerThingConfiguration> {

        public ModbusLoggerHandlerContext() {
            super(AbstractModbusLoggerHandler.this);
        }

        @SuppressWarnings("null")
        @Override
        protected Class<LoggerThingConfiguration> getConfigClass() {
            return LoggerThingConfiguration.class;
        }

    }

    private SendAndReciveStates toSendAndReciveStates(ModbusRegistryDefnition defnition) {
        SendingRequestState<ModbusLoggerHandlerContext> sendState = new SendingRequestState<>(
                defnition.getFirstRegister(), defnition.getLastRegister());
        ReadingResponseState<LoggerThingConfiguration, ModbusLoggerHandlerContext> readResponseState = new ReadingResponseState<>(
                ResponseDispatcher.create(defnition.getByteBufferConsumer()));
        return new SendAndReciveStates(null, sendState, readResponseState);
    }

    private class SendAndReciveStates {
        private final SendingRequestState<ModbusLoggerHandlerContext> sendState;
        private final ReadingResponseState<LoggerThingConfiguration, ModbusLoggerHandlerContext> responseState;

        public SendAndReciveStates(String description, SendingRequestState<ModbusLoggerHandlerContext> sendState,
                ReadingResponseState<LoggerThingConfiguration, ModbusLoggerHandlerContext> responseState) {
            this.sendState = sendState;
            this.responseState = responseState;
        }

        public SendingRequestState<ModbusLoggerHandlerContext> getSendState() {
            return sendState;
        }

        public ReadingResponseState<LoggerThingConfiguration, ModbusLoggerHandlerContext> getResponseState() {
            return responseState;
        }

    }

}
