package org.openhab.binding.lswlogger.internal.protocolv5.modbus;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.openhab.binding.lswlogger.internal.ChannelTypes;
import org.openhab.binding.lswlogger.internal.protocolv5.ChannelStateUpdate;
import org.openhab.core.types.State;

public class ModbusRegisterDefinitionBuilder {

    private final int firstRegister;
    private final int lastRegister;
    private final ChannelStateUpdate stateUpdate;
    private final List<ModbusRegistryValueDefinition> items = new ArrayList<>();
    private int firstBytesToEatCount;

    public ModbusRegisterDefinitionBuilder(int firstRegister, int lastRegister, ChannelStateUpdate stateUpdate) {
        this.firstRegister = firstRegister;
        this.lastRegister = lastRegister;
        this.stateUpdate = stateUpdate;
    }

    public ModbusRegisterDefinitionBuilder setFirstBytesEater(int firstBytesToEatCounst) {
        this.firstBytesToEatCount = firstBytesToEatCounst;
        return this;
    }

    public ModbusRegisterDefinitionBuilder add(int address, String channelId, String channelName,
            ChannelTypes channelType,
            Function<ByteBuffer, State> extractor) {
        items.add(new ModbusRegistryValueDefinition(address, channelId, channelName, channelType,
                buffer -> stateUpdate.apply(channelId, extractor.apply(buffer))));
        return this;
    }

    public ModbusRegistryDefinition build() {
        items.sort(Comparator.comparing(ModbusRegistryValueDefinition::getRegister));
        ByteBufferConsumer byteBufferConsumer = new ByteBufferConsumer(firstBytesToEatCount, firstRegister,
                lastRegister, items);
        ChannelConfigurer channelConfigurer = new ChannelConfigurer(items);
        return new ModbusRegistryDefinition(firstRegister, lastRegister, channelConfigurer, byteBufferConsumer);
    }

    public Consumer<ByteBuffer> shift(int count) {
        return buffer -> buffer.position(count + buffer.position());
    }

    public static class ModbusRegistryDefinition {
        private final int firstRegister;
        private final int lastRegister;
        private final ChannelConfigurer channelConfigurer;
        private final ByteBufferConsumer byteBufferConsumer;

        private ModbusRegistryDefinition(int firstRegister, int lastRegister, ChannelConfigurer channelConfigurer,
                ByteBufferConsumer byteBufferConsumer) {
            this.firstRegister = firstRegister;
            this.lastRegister = lastRegister;
            this.channelConfigurer = channelConfigurer;
            this.byteBufferConsumer = byteBufferConsumer;
        }

        public ChannelConfigurer getChannelConfigurer() {
            return channelConfigurer;
        }

        public ByteBufferConsumer getByteBufferConsumer() {
            return byteBufferConsumer;
        }

        public int getFirstRegister() {
            return firstRegister;
        }

        public int getLastRegister() {
            return lastRegister;
        }

    }

}
