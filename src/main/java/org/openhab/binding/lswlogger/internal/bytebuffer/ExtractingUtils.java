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
package org.openhab.binding.lswlogger.internal.bytebuffer;

import java.nio.ByteBuffer;
import java.util.function.Function;

import org.openhab.core.library.types.StringType;
import org.openhab.core.types.State;

public class ExtractingUtils {

    public static Function<ByteBuffer, State> bytesToHex() {
        return buffer -> {
            return StringType.valueOf(ByteUtils.toHex(buffer));
        };
    }

    public static Function<ByteBuffer, State> bytesToHex(int count) {
        return buffer -> {
            byte[] bytes = new byte[count];
            buffer.get(bytes);
            return StringType.valueOf(ByteUtils.toHex(ByteBuffer.wrap(bytes)));
        };
    }
}
