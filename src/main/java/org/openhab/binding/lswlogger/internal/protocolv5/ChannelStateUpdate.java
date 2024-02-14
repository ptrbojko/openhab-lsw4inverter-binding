package org.openhab.binding.lswlogger.internal.protocolv5;

import org.openhab.core.types.State;

public interface ChannelStateUpdate {
    void apply(String uuid, State state);
}