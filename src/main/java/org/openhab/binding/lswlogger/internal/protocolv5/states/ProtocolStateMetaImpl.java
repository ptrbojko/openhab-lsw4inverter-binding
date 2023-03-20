package org.openhab.binding.lswlogger.internal.protocolv5.states;

import org.openhab.binding.lswlogger.internal.connection.Context;

public class ProtocolStateMetaImpl<T, C extends Context<T>> implements ProtocolStateMeta<T, C> {
    private final String description;
    private final ProtocolState<T, C> state;
    private final ProtocolState<T, C> nextState;
    private final ProtocolState<T, C> alternativeState;
    private final ProtocolState<T, C> exceptioState;
    private final ProtocolState<T, C> errorState;

    public ProtocolStateMetaImpl(String description, ProtocolState<T, C> state, ProtocolState<T, C> nextState,
            ProtocolState<T, C> alternativeState,
            ProtocolState<T, C> exceptioState, ProtocolState<T, C> errorState) {
        this.description = description;
        this.state = state;
        this.nextState = nextState;
        this.alternativeState = alternativeState;
        this.exceptioState = exceptioState;
        this.errorState = errorState;
    }

    @Override
    public ProtocolState<T, C> getState() {
        return state;
    }

    @Override
    public ProtocolState<T, C> getNextState() {
        return nextState;
    }

    @Override
    public ProtocolState<T, C> getAlternativeState() {
        return alternativeState;
    }

    @Override
    public ProtocolState<T, C> getExceptioState() {
        return exceptioState;
    }

    @Override
    public ProtocolState<T, C> getErrorState() {
        return errorState;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
