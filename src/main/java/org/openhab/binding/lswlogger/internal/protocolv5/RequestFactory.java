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
    private static final byte SLAVE_ID = 0x01;
    private static final byte FUNCTION_CODE = 0x03;
    private static final byte CRC_PLACEHOLDER = (byte) 0xd8;
    private static final byte MSG_END = (byte) 0x15;

    public static ByteBuffer create(long sn, int fromRegister, int toRegister) {
        ByteBuffer buffer = ByteBuffer.allocate(LENGTH);
        putHeader(buffer); 
        putSerialNumber(sn, buffer);
        putSeparator(buffer);
        putCommand(fromRegister, toRegister - fromRegister, buffer);
        buffer.put(CRC_PLACEHOLDER);
        buffer.put(MSG_END);
        putCRC(1,34,buffer);
        return buffer;
    }

    private static void putSeparator(ByteBuffer buffer) {
        extracted(buffer);
    }

    private static void extracted(ByteBuffer buffer) {
        buffer.put(DATA_SEPARATOR);
    }

    private static void putSerialNumber(long sn, ByteBuffer buffer) {
        buffer.put(extractByte(sn));
        buffer.put(extractByte(sn >> 8));
        buffer.put(extractByte(sn >> 16));
        buffer.put(extractByte(sn >> 24));
    }

    private static void putHeader(ByteBuffer buffer) {
        buffer.put(HEADER);
    }

    private static void putCommand(int from, int length, ByteBuffer buffer) {
        int fromPosition = buffer.position();
        buffer.put(SLAVE_ID);
        buffer.put(FUNCTION_CODE);
        put2Bytes(from, buffer);
        put2Bytes(length, buffer);
        int toPosition = buffer.position();
        putModbusCRC(fromPosition, toPosition, buffer);
    }

    private static void put2Bytes(int data, ByteBuffer buffer) {
        buffer.put(extractByte(data >> 8));
        buffer.put(extractByte(data));
    }

    private static byte extractByte(int data) {
        return (byte) (data & 0xFF);
    }

    private static byte extractByte(long data) {
        return (byte) (data & 0xFF);
    }


    private static void putCRC(int fromPosition, int toPosition, ByteBuffer buffer) {
        int checksum = 0;
        buffer.position(fromPosition);
        while ( buffer.position() < toPosition) {
            checksum += buffer.get();
        }
        buffer.put((byte) (checksum & 0xff));
    }

    private static void putModbusCRC(int position, int toPosition, ByteBuffer data) {
        final int POLY = 0xA001;
        int crc = 0xFFFF;
        data.position(position);
        while (data.position() < toPosition) {
            byte b = data.get();
            crc ^= b;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x0001) != 0) {
                    crc = (crc >> 1) ^ POLY;
                } else {
                    crc = crc >> 1;
                }
            }
        }
        data.put((byte) (crc & 0xFF)).put((byte) ((crc >> 8) & 0xFF));
    }
}
