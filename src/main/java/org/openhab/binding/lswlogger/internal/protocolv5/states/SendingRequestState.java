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

import org.openhab.binding.lswlogger.internal.LoggerThingConfiguration;
import org.openhab.binding.lswlogger.internal.connection.Context;
import org.openhab.binding.lswlogger.internal.connection.StateMachineSwitchable;

public class SendingRequestState<C extends Context<LoggerThingConfiguration>>
        extends AbstractSendingRequestState<LoggerThingConfiguration, C> {

    private final int fromRegister;
    private final int toRegister;

    public SendingRequestState(int fromRegister, int toRegister) {
        this.fromRegister = fromRegister;
        this.toRegister = toRegister;
    }

    @Override
    protected int getFromRegister( StateMachineSwitchable sm,  C context) {
        return fromRegister;
    }

    @Override
    protected int getToRegister( StateMachineSwitchable sm,  C context) {
        return toRegister;
    }

}
