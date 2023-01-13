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

public class RequestFactory {

    private static final int LENGTH = 36;
    private static final byte[] HEADER = new byte[] { (byte) 0xa5, 0x17, 0x00, 0x10, 0x45, 0x00, 0x00 };
    private static final byte[] DATA_SEPARATOR = new byte[] { 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
    private static final byte[] COMMAND = new byte[] { 0x01, 0x03, 0x00, 0x00, 0x00, 0x27, 0x05, (byte) 0xd0 };
    private static final byte CRC_PLACEHOLDER = (byte) 0xd8;
    private static final byte MSG_END = (byte) 0x15;
    private static final int CRC_POS = 34;

    public static ByteBuffer create(int sn) {
        ByteBuffer buffer = ByteBuffer.allocate(LENGTH);
        buffer.put(HEADER);
        buffer.put(createSNPart(sn));
        buffer.put(DATA_SEPARATOR);
        buffer.put(COMMAND);
        buffer.put(CRC_PLACEHOLDER);
        buffer.put(MSG_END);
        putCRC(buffer);
        return buffer;
    }

    private static void putCRC(ByteBuffer buffer) {
        int checksum = 0;
        for (int l = 1; l < 34; l++) {
            checksum += (buffer.get(l) & 0xff);
        }
        buffer.put(CRC_POS, (byte) (checksum & 0xff));
    }

    private static byte[] createSNPart(int sn) {
        byte[] snBytes = ByteBuffer.allocate(4).putInt(sn).array();
        reverse(snBytes);
        return snBytes;
    }

    private static void reverse(byte[] snBytes) {
        for (int a = 0, b = snBytes.length - 1; a < b; a++, b--) {
            byte tmp = snBytes[a];
            snBytes[a] = snBytes[b];
            snBytes[b] = tmp;
        }
    }
}
