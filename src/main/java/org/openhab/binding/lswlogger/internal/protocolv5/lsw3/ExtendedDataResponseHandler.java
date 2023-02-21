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
package org.openhab.binding.lswlogger.internal.protocolv5.lsw3;

import java.nio.ByteBuffer;

import org.openhab.binding.lswlogger.internal.protocolv5.ExtractorsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtendedDataResponseHandler extends DataResponseHandler {

    private static final Logger logger = LoggerFactory.getLogger(ExtendedDataResponseHandler.class);

    public static final int RESPONSE_LENGTH = 124;

    public ExtendedDataResponseHandler(ExtractorsBuilder.ChannelStateUpdate stateUpdate) {
        super(stateUpdate);
    }

    @Override
    protected int getResponseLength() {
        return RESPONSE_LENGTH;
    }

    @Override
    protected void performActionsBeforeExtraction(ByteBuffer buffer) {
        logger.debug("Extended response from logger received, length {} bytes", RESPONSE_LENGTH);
    }
}
