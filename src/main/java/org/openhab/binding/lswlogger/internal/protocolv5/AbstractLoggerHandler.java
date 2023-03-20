package org.openhab.binding.lswlogger.internal.protocolv5;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.lswlogger.internal.LoggerThingConfiguration;
import org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants;
import org.openhab.binding.lswlogger.internal.connection.Channel;
import org.openhab.binding.lswlogger.internal.connection.Context;
import org.openhab.binding.lswlogger.internal.protocolv5.states.StateMachine;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;

public abstract class AbstractLoggerHandler extends BaseThingHandler {

    private StateMachine<?, ?> stateMachine;

    public AbstractLoggerHandler(Thing thing) {
        super(thing);
    }

    @Override
    public final void initialize() {
        updateStatus(ThingStatus.UNKNOWN);
        disconnectWhenNeeded();
        stateMachine = createStateMachine();
        scheduler.execute(stateMachine::run);
    }

    protected abstract StateMachine<?, ?> createStateMachine();

    private void disconnectWhenNeeded() {
        if (stateMachine != null) {
            stateMachine.stop();
        }
    }

    @Override
    public final void handleRemoval() {
        super.handleRemoval();
        disconnectWhenNeeded();
    }

    @Override
    public final void dispose() {
        super.dispose();
        disconnectWhenNeeded();
    }

    @Override
    public final void handleCommand(ChannelUID channelUID, Command command) {
        // no op
    }

    public static class AbstractContext implements Context<LoggerThingConfiguration> {

        private final AbstractLoggerHandler handler;

        private Channel channel;

        public AbstractContext(AbstractLoggerHandler handler) {
            this.handler = handler;
            this.channel = new Channel();
        }

        @Override
        public final Channel channel() {
            return channel;
        }

        @Override
        public void notifyLoggerIsOffline() {
            updateState(LswLoggerBindingConstants.Common.onlineChannel, OnOffType.OFF);
        }

        @Override
        public final void notifyCannotRecover() {
            handler.updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
        }

        @Override
        public final void updateState(@NonNull String uuid, @NonNull State state) {
            handler.updateState(uuid, state);
        }

        @Override
        public final void updateStatus(@NonNull ThingStatus status) {
            handler.updateStatus(status);
        }

        @Override
        public LoggerThingConfiguration config() {
            return handler.getConfigAs(LoggerThingConfiguration.class);
        }

    }

}
