package org.openhab.binding.lswlogger.internal.protocolv5.states;

import java.util.Map;

import org.openhab.binding.lswlogger.internal.LoggerThingConfiguration;
import org.openhab.binding.lswlogger.internal.connection.Context;
import org.openhab.binding.lswlogger.internal.connection.StateMachineSwitchable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StateMachine<C extends Context> implements StateMachineSwitchable {

    private static final Logger logger = LoggerFactory.getLogger(StateMachine.class);
    
    private final C context;
    private final LoggerThingConfiguration configuration;
    private final Map<ProtocolState<C>, ProtocolStateMeta<C>> states;
    private ProtocolStateMeta<C> current;   
    private boolean closed = false;
    private final ProtocolState<C> initialState;

    public StateMachine(C context, LoggerThingConfiguration configuration,
            Map<ProtocolState<C>, ProtocolStateMeta<C>> states, ProtocolState<C> initialState) {
        this.context = context;
        this.configuration = configuration;
        this.states = states;
        this.initialState = initialState;
    }

    @Override
    public void switchToNextState() {
        switchTo(current.getNextState());
    }

    @Override
    public void switchToAlternativeState() {
        switchTo(current.getAlternativeState());
    }

    @Override
    public void switchToExceptionState() {
        switchTo(current.getExceptioState());
    }

    @Override
    public void switchToErrorState() {
        switchTo(current.getErrorState());        
    }

    public void close() {
        closed = true;
        if (current != null) {
            current.getState().close(context, configuration);
        }
    }

    public void start() {
        switchTo(initialState);
    }

    private void switchTo(ProtocolState<C> state) {
        if (closed) {
            logger.warn("Context is closed, not switching to {}", state);
            return;
        }
        ProtocolStateMeta<C> newMeta = states.get(state);
        logger.debug("Ticking {}", newMeta.getState());
        newMeta.getState().tick(this, context, configuration);
    }
    
}
