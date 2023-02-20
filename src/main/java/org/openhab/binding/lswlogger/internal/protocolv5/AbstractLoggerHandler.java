package org.openhab.binding.lswlogger.internal.protocolv5;

import java.nio.ByteBuffer;

import org.openhab.binding.lswlogger.internal.LoggerThingConfiguration;
import org.openhab.binding.lswlogger.internal.connection.Context;
import org.openhab.binding.lswlogger.internal.protocolv5.lsw3.LswLoggerHandler;
import org.openhab.binding.lswlogger.internal.protocolv5.states.ProtocolState;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLoggerHandler extends BaseThingHandler {

    private static final Logger logger = LoggerFactory.getLogger(LswLoggerHandler.class);

    private final ResponseDispatcher responseDispatcher;
    private final ByteBuffer response = ByteBuffer.allocate(1024);
    private Context context;
    private LoggerThingConfiguration configuration;
    private ByteBuffer request;

    public AbstractLoggerHandler(Thing thing, ResponseDispatcher responseDispatcher) {
        super(thing);
        this.responseDispatcher = responseDispatcher;
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.UNKNOWN);
        disconnectWhenNeeded();
        context = createContext();
        configuration = this.getConfigAs(LoggerThingConfiguration.class);
        request = createRequest();
        request.flip();
        StateMaBui
    }

    protected abstract ByteBuffer createRequest();

    protected abstract Context createContext();

    protected abstract ProtocolState createInitialState();

    private void disconnectWhenNeeded() {
        if (context != null) {
            context.close();
        }
    }

    @Override
    public void handleRemoval() {
        super.handleRemoval();
        disconnectWhenNeeded();
    }

    @Override
    public void dispose() {
        super.dispose();
        disconnectWhenNeeded();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // no op
    }

}
