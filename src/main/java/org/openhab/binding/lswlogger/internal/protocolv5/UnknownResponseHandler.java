/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
 * <p>
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.lswlogger.internal.protocolv5;

import java.nio.ByteBuffer;

import org.openhab.binding.lswlogger.internal.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnknownResponseHandler implements ResponseHandler {

    private static final Logger logger = LoggerFactory.getLogger(UnknownResponseHandler.class);

    private final String thingId;

    public UnknownResponseHandler(String thingId) {
        this.thingId = thingId;
    }

    @Override
    public boolean accept(ByteBuffer buffer) {
        logger.warn("Unknown response from logger {}, length {}: {}", thingId, buffer.remaining(),
                ByteUtils.toHex(buffer));
        return true;
    }
}
