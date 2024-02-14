package org.openhab.binding.lswlogger.internal.protocolv5.thing.debug;

import org.openhab.binding.lswlogger.internal.connection.Context;
import org.openhab.binding.lswlogger.internal.connection.StateMachineSwitchable;
import org.openhab.binding.lswlogger.internal.protocolv5.states.AbstractSendingRequestState;

public class ConfigurableSendingRequestState<C extends Context<DebugLoggerThingConfiguration>>
        extends AbstractSendingRequestState<DebugLoggerThingConfiguration, C> {

    @Override
    protected int getFromRegister(StateMachineSwitchable sm, C context) {
        return Integer.decode(context.config().getStartRegister());
    }

    @Override
    protected int getToRegister(StateMachineSwitchable sm, C context) {
        return Integer.decode(context.config().getEndRegister());
    }

}
