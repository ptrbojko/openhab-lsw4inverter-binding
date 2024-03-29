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
package org.openhab.binding.lswlogger.internal.protocolv5;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDataResponseHandler implements ResponseHandler {

    private final static Logger logger = LoggerFactory.getLogger(AbstractDataResponseHandler.class);

    @Override
    public boolean handle(ByteBuffer buffer) {
        if (!accepts(buffer)) {
            return false;
        }
        extract(buffer);
        return true;
    }

    protected abstract boolean accepts(ByteBuffer buffer);

    protected abstract void accept(ByteBuffer byteBuffer);

    private void extract(ByteBuffer buffer) {
        logger.debug("Trying to extract message, length {} bytes", buffer.remaining());
        performActionsBeforeExtraction(buffer);
        accept(buffer);
    }

    protected void performActionsBeforeExtraction(ByteBuffer buffer) {
        // no op
    };
}
