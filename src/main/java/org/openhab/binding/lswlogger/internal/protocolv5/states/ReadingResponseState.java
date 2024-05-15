package org.openhab.binding.lswlogger.internal.protocolv5.states;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import org.openhab.binding.lswlogger.internal.LoggerThingConfiguration;
import org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.Common;
import org.openhab.binding.lswlogger.internal.connection.Channel.ChannelReader;
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

    private final ResponseDispatcher responseDispatcher;

    public ReadingResponseState(ResponseDispatcher responseDispatcher) {
        this.responseDispatcher = responseDispatcher;
    }

    @Override
    public void handle(StateMachineSwitchable sm, C context) {
        new ReadingHandler(sm, context, context.config().getRefreshTime()).run();
    }

    private class ReadingHandler implements ChannelReader {

        private final StateMachineSwitchable sm;
        private final C context;
        private long period;

        public ReadingHandler(StateMachineSwitchable sm, C context, long period) {
            this.period = period;
            this.sm = sm;
            this.context = context;
        }

        public void run() {
            context.channel().startReading(this);
            scheduleTimeoutForReadingAndHeadingToNextState();
        }

        private void scheduleTimeoutForReadingAndHeadingToNextState() {
            sm.schedule(period, TimeUnit.SECONDS, () -> {
                logger.debug("Stopping reading and heading to next state");
                context.channel().stopReading();
                sm.switchToNextState();
            });
        }

        @Override
        public void onRead(ByteBuffer message) {
            logger.debug("Trying to read a message");
            context.updateState(Common.onlineChannel, OnOffType.ON);
            context.updateStatus(ThingStatus.ONLINE);
            responseDispatcher.accept(message);
        }

        @Override
        public void onFail(Throwable throwable) {
            logger.error("Problem reading response data", throwable);
            context.channel().stopReading();
            sm.switchToExceptionState();
        }

        @Override
        public void transfer(ChannelReader reader) {
            // no op
        }
    }
}
