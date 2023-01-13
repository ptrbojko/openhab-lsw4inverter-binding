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
package org.openhab.binding.lswlogger.internal;

import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.OPERATING_STATES;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.function.Function;

import javax.measure.quantity.Temperature;

import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.library.unit.SIUnits;
import org.openhab.core.library.unit.Units;
import org.openhab.core.types.State;

public class ExtractingUtils {

    public static QuantityType<Temperature> extractShortToTemperature(ByteBuffer buffer) {
        return new QuantityType<>(buffer.getShort(), SIUnits.CELSIUS);
    }

    public static State extractShortAsMinute(ByteBuffer buffer) {
        return new QuantityType<>(buffer.getShort(), Units.MINUTE);
    }

    public static State extractIntToHours(ByteBuffer buffer) {
        return new QuantityType<>(buffer.getInt(), Units.HOUR);
    }

    public static State extractIntToDecimalAsKiloWattHour(ByteBuffer buffer) {
        return new QuantityType<>(buffer.getInt(), Units.KILOWATT_HOUR);
    }

    public static StringType extractShortToToStringType(ByteBuffer buffer) {
        return StringType.valueOf(OPERATING_STATES[buffer.getShort()]);
    }

    public static State shortToHundrethsAsKiloWattHour(ByteBuffer buffer) {
        return new QuantityType<>(buffer.getShort() * 10f, Units.WATT_HOUR);
    }

    public static State shortToHundrethsAsHz(ByteBuffer buffer) {
        return new QuantityType<>(buffer.getShort() / 100f, Units.HERTZ);
    }

    public static State shortMultiple10AsWatts(ByteBuffer buffer) {
        return new QuantityType<>(buffer.getShort() * 10f, Units.WATT);
    }

    public static State shortMultiple10AsVars(ByteBuffer buffer) {
        return new QuantityType<>(buffer.getShort() * 10f, Units.VOLT_AMPERE_HOUR);
    }

    public static State shortToHundrethsAsAmpers(ByteBuffer buffer) {
        return new QuantityType<>(buffer.getShort() / 100f, Units.AMPERE);
    }

    public static State shortToTenthsAsVoltage(ByteBuffer buffer) {
        return new QuantityType<>(BigDecimal.valueOf(buffer.getShort() / 10f), Units.VOLT);
    }

    public static Function<ByteBuffer, State> bytesToString(int i) {
        return buffer -> {
            byte[] bytes = new byte[i];
            buffer.get(bytes);
            return StringType.valueOf(Charset.forName("IBM437").decode(ByteBuffer.wrap(bytes)).toString());
        };
    }
}
