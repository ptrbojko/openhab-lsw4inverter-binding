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

import java.io.IOException;
import java.net.InetSocketAddress;

import org.openhab.binding.lswlogger.internal.LoggerThingConfiguration;
import org.openhab.binding.lswlogger.internal.connection.Context;
import org.openhab.binding.lswlogger.internal.connection.StateMachineSwitchable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectingState<C extends Context> implements ProtocolState<C> {

    private static final Logger logger = LoggerFactory.getLogger(ConnectingState.class);

    @Override
    public void tick(StateMachineSwitchable sm, C context, LoggerThingConfiguration configuration) {
        context.channel().reopen(new InetSocketAddress(configuration.getHostname(), configuration.getPort()),
                () -> sm.switchToNextState(),
                t -> {
                    logger.error("Cannot open nio channel", t);
                    sm.switchToErrorState();
                },
                t -> {
                    logger.error("Error connecting", t);
                    sm.switchToExceptionState();
                });
    }

    @Override
    public void close(C context, LoggerThingConfiguration configuration) {
        try {
            context.channel().close();
        } catch (IOException e) {
            logger.error("Cannot disconnect from channel", e);
        }
    }

}
