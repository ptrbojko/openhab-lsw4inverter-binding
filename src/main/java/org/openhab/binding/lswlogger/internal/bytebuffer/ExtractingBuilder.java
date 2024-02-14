package org.openhab.binding.lswlogger.internal.bytebuffer;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.function.Function;

import javax.measure.Unit;

import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.types.State;

public class ExtractingBuilder {

    public static ShortExtractor extactShort() {
        return new ShortExtractor(ByteBuffer::getShort);
    }

    public static IntExtractor extractInteger() {
        return new IntExtractor(ByteBuffer::getInt);
    }

    public static ByteExtractor extractBytes(int count) {
        return new ByteExtractor(buffer -> {
            byte[] bytes = new byte[count];
            buffer.get(bytes);
            return ByteBuffer.wrap(bytes);
        });
    }

    public ByteExtractor extractBytes() {
        return new ByteExtractor(Function.identity());
    }

    public static class ByteExtractor {
        private final Function<ByteBuffer, ByteBuffer> bFunction;

        public ByteExtractor(Function<ByteBuffer, ByteBuffer> bFunction) {
            this.bFunction = bFunction;
        }

        public Function<ByteBuffer, State> asString() {
            return buffer -> {
                ByteBuffer internalBuffer = bFunction.apply(buffer);
                return StringType.valueOf(Charset.forName("IBM437").decode(internalBuffer).toString());
            };
        }

        public Function<ByteBuffer, State> asHex() {
            return bFunction.andThen(buffer -> StringType.valueOf(ByteUtils.toHex(buffer)));
        }
    }

    public static class ShortExtractor {
        private Function<ByteBuffer, Short> bFunction;

        public ShortExtractor(Function<ByteBuffer, Short> bFunction) {
            this.bFunction = bFunction;
        }

        public Function<ByteBuffer, State> as(Unit<?> uom) {
            return bFunction.andThen(v -> new QuantityType<>(v, uom));
        }

        public IntExtractor multiplied(int i) {
            return new IntExtractor(bFunction.<Integer>andThen(v -> v * i));
        }

        public BigDecimalExtractor divided(float f) {
            return new BigDecimalExtractor(bFunction.andThen(v -> BigDecimal.valueOf(v / f)));
        }

        public Function<ByteBuffer, State> mappedTo(String[] map) {
            return bFunction.andThen(s -> StringType.valueOf(map[s]));
        }

    }

    public static class IntExtractor {
        private Function<ByteBuffer, Integer> bFunction;

        public IntExtractor(Function<ByteBuffer, Integer> bFunction) {
            this.bFunction = bFunction;
        }

        public IntExtractor revertedShorts() {
            bFunction = bFunction.andThen(i -> (i << 16) | ((i & 0xffff0000) >> 16));
            return this;
        }

        public IntExtractor multiplied(int i) {
            return new IntExtractor(bFunction.<Integer>andThen(v -> v * i));
        }

        public BigDecimalExtractor divided(float f) {
            return new BigDecimalExtractor(bFunction.andThen(v -> BigDecimal.valueOf(v / f)));
        }

        public Function<ByteBuffer, State> as(Unit<?> uom) {
            return bFunction.andThen(v -> new QuantityType<>(v, uom));
        }
    }

    public static class BigDecimalExtractor {

        private final Function<ByteBuffer, BigDecimal> bFunction;

        public BigDecimalExtractor(Function<ByteBuffer, BigDecimal> bFunction) {
            this.bFunction = bFunction;
        }

        public Function<ByteBuffer, State> as(Unit<?> uom) {
            return bFunction.andThen(v -> new QuantityType<>(v, uom));
        }

    }

}
