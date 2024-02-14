package org.openhab.binding.lswlogger.internal.protocolv5.modbus;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import org.openhab.binding.lswlogger.internal.ChannelTypes;

class ModbusRegistryValueDefinition {
    private final long register;
    private final Consumer<ByteBuffer> extractor;
    private final String channelName;
    private final ChannelTypes channelType;
    private final String channelId;

    public ModbusRegistryValueDefinition(long register, String channelId, String channelName, ChannelTypes channelType,
            Consumer<ByteBuffer> extractor) {
        this.register = register;
        this.channelId = channelId;
        this.channelName = channelName;
        this.channelType = channelType;
        this.extractor = extractor;
    }

    public long getRegister() {
        return register;
    }

    public String getChannelName() {
        return channelName;
    }

    public ChannelTypes getChannelType() {
        return channelType;
    }

    public String getChannelId() {
        return channelId;
    }

    public void accept(ByteBuffer buffer) {
        extractor.accept(buffer);
    }

}