/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.lswlogger.internal.protocolv5.states;

import java.nio.ByteBuffer;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.lswlogger.internal.LoggerThingConfiguration;
import org.openhab.binding.lswlogger.internal.connection.Context;
import org.openhab.binding.lswlogger.internal.connection.StateMachineSwitchable;
import org.openhab.binding.lswlogger.internal.protocolv5.RequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendingRequestState<C extends Context<LoggerThingConfiguration>>
        implements ProtocolState<LoggerThingConfiguration, C> {

    private static final Logger logger = LoggerFactory.getLogger(SendingRequestState.class);

    private final int fromRegister;
    private final int toRegister;

    public SendingRequestState(int fromRegister, int toRegister) {
        this.fromRegister = fromRegister;
        this.toRegister = toRegister;
    }

    @Override
    public void handle(@NonNull StateMachineSwitchable sm, @NonNull C context) {
        ByteBuffer request = RequestFactory
                .create(context.config().getSerialNumber(), fromRegister, toRegister)
                .flip()
                .clear();
        context.channel().write(request, sm::switchToNextState, t -> {
            logger.error("Failed to write to channel", t);
            sm.switchToExceptionState();
        });
    }

}
