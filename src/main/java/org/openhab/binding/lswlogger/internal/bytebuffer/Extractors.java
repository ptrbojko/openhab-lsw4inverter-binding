package org.openhab.binding.lswlogger.internal.bytebuffer;

import java.nio.ByteBuffer;
import java.util.function.Function;

import org.openhab.core.library.unit.Units;
import org.openhab.core.types.State;

public class Extractors {

        public static final Function<ByteBuffer, State> SHORT_AS_HUNDRETHS_OF_AMPERE = ExtractingBuilder.extactShort()
                        .divided(100).as(Units.AMPERE);

        public static final Function<ByteBuffer, State> SHORT_AS_TENTHS_OF_AMPERE = ExtractingBuilder.extactShort()
                        .divided(10).as(Units.AMPERE);

        public static final Function<ByteBuffer, State> SHORT_AS_TEHNTS_OF_VOLT = ExtractingBuilder.extactShort()
                        .divided(10).as(Units.VOLT);

        public static final Function<ByteBuffer, State> SHORT_AS_THENTS_OF_WATT_HOUR = ExtractingBuilder.extactShort()
                        .divided(10).as(Units.WATT_HOUR);

        public static final Function<ByteBuffer, State> INT_AS_TENTHS_OF_KILOWATT_HOUR = ExtractingBuilder.extractInteger()
                        .divided(10).as(Units.KILOWATT_HOUR);

        public static final Function<ByteBuffer, State> INT_AS_HUNDRETHS_OF_KILOWATT_HOUR = ExtractingBuilder.extractInteger()
                        .divided(100).as(Units.KILOWATT_HOUR);

        public static final Function<ByteBuffer, State> SHORT_AS_DOZENS_WATT = ExtractingBuilder.extactShort()
                        .multiplied(10).as(Units.WATT);

        public static final Function<ByteBuffer, State> HUNDRETHS_OF_HERZ = ExtractingBuilder.extactShort().divided(100)
                        .as(Units.HERTZ);

        public static final Function<ByteBuffer, State> THOUSANDS_AS_FACTOR = ExtractingBuilder.extactShort()
                        .divided(1000).as(Units.ONE);
}
