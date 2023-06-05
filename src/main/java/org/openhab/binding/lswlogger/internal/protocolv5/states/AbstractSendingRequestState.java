package org.openhab.binding.lswlogger.internal.protocolv5.states;

import java.nio.ByteBuffer;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.lswlogger.internal.LoggerThingConfiguration;
import org.openhab.binding.lswlogger.internal.connection.Context;
import org.openhab.binding.lswlogger.internal.connection.StateMachineSwitchable;
import org.openhab.binding.lswlogger.internal.protocolv5.RequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSendingRequestState<LC extends LoggerThingConfiguration, C extends Context<LC>>
        implements ProtocolState<LC, C> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSendingRequestState.class);

    @Override
    public final void handle(@NonNull StateMachineSwitchable sm, @NonNull C context) {
        ByteBuffer request = RequestFactory
                .create(context.config().getSerialNumber(), getFromRegister(sm, context), getToRegister(sm, context))
                .flip()
                .clear();
        context.channel().write(request, sm::switchToNextState, t -> {
            logger.error("Failed to write to channel", t);
            sm.switchToExceptionState();
        });
    }

    protected abstract int getFromRegister(@NonNull StateMachineSwitchable sm, @NonNull C context);

    protected abstract int getToRegister(@NonNull StateMachineSwitchable sm, @NonNull C context);
}