package org.openhab.binding.lswlogger.internal.protocolv5.states;

import org.openhab.binding.lswlogger.internal.connection.Context;

public interface ProtocolStateMeta<T, C extends Context<T>> {

    ProtocolState<T, C> getState();

    ProtocolState<T, C> getNextState();

    ProtocolState<T, C> getAlternativeState();

    ProtocolState<T, C> getExceptioState();

    ProtocolState<T, C> getErrorState();

    String getDescription();

}