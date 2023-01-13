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
package org.openhab.binding.lswlogger.internal;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ByteUtils {
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);

    public static String toHex(ByteBuffer buffer) {
        int length = buffer.remaining() + 1;
        byte[] hexChars = new byte[length * 2];
        while (buffer.hasRemaining()) {
            int v = buffer.get() & 0xFF;
            hexChars[buffer.position() * 2] = HEX_ARRAY[v >>> 4];
            hexChars[buffer.position() * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }
}
