package org.openhab.binding.lswlogger.internal.protocolv5;

import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNull;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLoggerHandler extends BaseThingHandler {

    private static final Logger logger = LoggerFactory.getLogger(AbstractLoggerHandler.class);

    private StateMachine<?> stateMachine;

    public AbstractLoggerHandler(Thing thing) {
        super(thing);
    }

    @Override
    public final void initialize() {
        updateStatus(ThingStatus.UNKNOWN);
        disconnectWhenNeeded();
        stateMachine = createStateMachine();
        stateMachine.start();
    }

    protected abstract StateMachine<?> createStateMachine();

    private void disconnectWhenNeeded() {
        if (stateMachine != null) {
            stateMachine.close();
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

    public static class AbstractContext implements Context {

        private final AbstractLoggerHandler handler;

        private Channel channel;

        public AbstractContext(AbstractLoggerHandler handler) {
            this.handler = handler;
            this.channel = new Channel();
        }

        @Override
        public final void schedule(int i, TimeUnit unit, Runnable runnable) {
            handler.scheduler.schedule(runnable, i, unit);
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

    }

}
