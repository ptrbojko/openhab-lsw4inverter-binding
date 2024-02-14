package org.openhab.binding.lswlogger.internal.protocolv5.modbus;

import java.nio.ByteBuffer;
import java.util.List;

import org.openhab.binding.lswlogger.internal.protocolv5.AbstractDataResponseHandler;

class ByteBufferConsumer extends AbstractDataResponseHandler {

    private final int headerSize;
    private final long firstAddress;
    private final long lastAddres;
    private final List<ModbusRegistryValueDefinition> mrs;

    public ByteBufferConsumer(int headerSize, long firstAddress, long lastAddres,
            List<ModbusRegistryValueDefinition> mrs) {
        this.headerSize = headerSize;
        this.firstAddress = firstAddress;
        this.lastAddres = lastAddres;
        this.mrs = mrs;

    }

    @Override
    protected final void accept(ByteBuffer buffer) {
        mrs.forEach(mr -> {
            int position = getPositionForAddress(mr.getRegister());
            if (buffer.position() > position) {
                throw new IllegalStateException("Position ahead of addres " + mr.getRegister() + ", current "
                        + buffer.position() + ", wanted " + position);
            }
            if (buffer.position() < position) {
                buffer.position(position); // todo: is it fast?
            }
            mr.accept(buffer);
        });
    }

    private int getPositionForAddress(long address) {
        return Math.toIntExact(headerSize + (2 * (address - firstAddress)));
    }

    @Override
    protected boolean accepts(ByteBuffer buffer) {
        return buffer.remaining() >= headerSize + ((lastAddres - firstAddress) / 2);
    }

}
