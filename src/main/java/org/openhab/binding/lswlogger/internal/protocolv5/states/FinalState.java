package org.openhab.binding.lswlogger.internal.protocolv5.states;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.lswlogger.internal.connection.Context;
import org.openhab.binding.lswlogger.internal.connection.StateMachineSwitchable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FinalState<T, C extends Context<T>> implements ProtocolState<T, C> {

    private static final Logger logger = LoggerFactory.getLogger(FinalState.class);

    @Override
    public void handle(@NonNull StateMachineSwitchable stateMachine, @NonNull C context) {
        logger.info("Final state. No next steps should be processed");
    }

}
