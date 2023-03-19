package org.openhab.binding.lswlogger.internal.protocolv5.states;

import org.openhab.binding.lswlogger.internal.connection.Context;

public class ProtocolStateMeta<T, C extends Context<T>> {
    private final ProtocolState<T, C> state;
    private final ProtocolState<T, C> nextState;
    private final ProtocolState<T, C> alternativeState;
    private final ProtocolState<T, C> exceptioState;
    private final ProtocolState<T, C> errorState;

    public ProtocolStateMeta(ProtocolState<T, C> state, ProtocolState<T, C> nextState,
            ProtocolState<T, C> alternativeState,
            ProtocolState<T, C> exceptioState, ProtocolState<T, C> errorState) {
        this.state = state;
        this.nextState = nextState;
        this.alternativeState = alternativeState;
        this.exceptioState = exceptioState;
        this.errorState = errorState;
    }

    public ProtocolState<T, C> getState() {
        return state;
    }

    public ProtocolState<T, C> getNextState() {
        return nextState;
    }

    public ProtocolState<T, C> getAlternativeState() {
        return alternativeState;
    }

    public ProtocolState<T, C> getExceptioState() {
        return exceptioState;
    }

    public ProtocolState<T, C> getErrorState() {
        return errorState;
    }

}
