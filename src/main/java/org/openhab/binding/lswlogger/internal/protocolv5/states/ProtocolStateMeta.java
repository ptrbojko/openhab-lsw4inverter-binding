package org.openhab.binding.lswlogger.internal.protocolv5.states;

import org.openhab.binding.lswlogger.internal.connection.Context;

public class ProtocolStateMeta<C extends Context> {
    private final ProtocolState<C> state;
    private final ProtocolState<C> nextState;
    private final ProtocolState<C> alternativeState; 
    private final ProtocolState<C> exceptioState;
    private final ProtocolState<C> errorState;
    public ProtocolStateMeta(ProtocolState<C> state, ProtocolState<C> nextState, ProtocolState<C> alternativeState,
            ProtocolState<C> exceptioState, ProtocolState<C> errorState) {
        this.state = state;
        this.nextState = nextState;
        this.alternativeState = alternativeState;
        this.exceptioState = exceptioState;
        this.errorState = errorState;
    }
    public ProtocolState<C> getState() {
        return state;
    }
    public ProtocolState<C> getNextState() {
        return nextState;
    }
    public ProtocolState<C> getAlternativeState() {
        return alternativeState;
    }
    public ProtocolState<C> getExceptioState() {
        return exceptioState;
    }
    public ProtocolState<C> getErrorState() {
        return errorState;
    }

}
