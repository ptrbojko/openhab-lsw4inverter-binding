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
        byte[] hexChars = new byte[buffer.remaining() * 3];
        while (buffer.hasRemaining()) {
            int position = buffer.position();
            int v = buffer.get() & 0xFF;
            hexChars[position * 3] = HEX_ARRAY[v >>> 4];
            hexChars[position * 3 + 1] = HEX_ARRAY[v & 0x0F];
            hexChars[position * 3 + 2] = ':';
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }

    public static ByteBuffer fromHex(String msg) {
        String[] strings = msg.split(":");
        ByteBuffer result = ByteBuffer.allocate(strings.length);
        for (String str : strings) {
            result.put((byte) ((Character.digit(str.charAt(0), 16) << 4) + Character.digit(str.charAt(1), 16)));
        }
        return result;
    }

}
