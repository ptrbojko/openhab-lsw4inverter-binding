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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(ResponseDispatcher.class);
    private final List<ResponseHandler> handlers;

    public ResponseDispatcher(List<ResponseHandler> handlers) {
        this.handlers = handlers;
    }

    public void accept(ByteBuffer buffer) {
        for (ResponseHandler handler : handlers) {
            buffer.rewind();
            if (handler.accept(buffer)) {
                logger.debug("Handler {} matched response", handler.getClass());
                break;
            }
        }
    }

    public static ResponseDispatcher create(ResponseHandler... handlers) {
        return new ResponseDispatcher(List.of(handlers));
    }
}
