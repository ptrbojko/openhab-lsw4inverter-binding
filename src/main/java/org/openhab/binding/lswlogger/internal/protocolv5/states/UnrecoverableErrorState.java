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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnrecoverableErrorState<C extends Context> implements ProtocolState<C> {

    private static final Logger logger = LoggerFactory.getLogger(UnrecoverableErrorState.class);

    @Override
    public void tick(StateMachineSwitchable sm, C context, LoggerThingConfiguration configuration) {
        logger.error("Giving up connecting to logger");
        context.notifyCannotRecover();
    }

    @Override
    public void close(C context, LoggerThingConfiguration configuration) {
    }
}
