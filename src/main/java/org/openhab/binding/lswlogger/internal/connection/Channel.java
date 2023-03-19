package org.openhab.binding.lswlogger.internal.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Channel {

    private static final int BUFFERS_COUNT = 4;

    private static final int SENDING_TIMEOUT = 10;

    private final ArrayBlockingQueue<ByteBuffer> buffers = new ArrayBlockingQueue<>(BUFFERS_COUNT);
    private final ArrayBlockingQueue<ByteBuffer> messages = new ArrayBlockingQueue<>(BUFFERS_COUNT);

    private AsynchronousSocketChannel chnl;
    private Throwable throwable;

    public Channel() {
        Stream.generate(() -> ByteBuffer.allocate(1024)).limit(BUFFERS_COUNT).forEach(buffers::add);
    }

    public synchronized void reopen(InetSocketAddress address, Runnable connectedHandler,
            Consumer<Throwable> failedOpenHandler, Consumer<Throwable> failedConnectHandler) {
        try {
            throwable = null;
            if (chnl != null && chnl.isOpen()) {
                chnl.close();
            }
            chnl = AsynchronousSocketChannel.open();
            chnl.connect(address, null, new CompletionHandler<Void, Void>() {

                @Override
                public void completed(Void v1, Void v2) {
                    connectedHandler.run();
                    listen();
                }

                @Override
                public void failed(Throwable t, Void v) {
                    failedConnectHandler.accept(t);
                }

            });
        } catch (Exception e) {
            failedOpenHandler.accept(e);
        }
    }

    private void listen() {
        ByteBuffer buffer = buffers.remove();
        buffer.clear();
        chnl.read(buffer, null, new CompletionHandler<Integer, Void>() {

            @Override
            public void completed(Integer bytesRead, Void attachment) {
                buffer.flip();
                try {
                    messages.put(buffer);
                    if (chnl.isOpen()) {
                        listen();
                    }
                } catch (InterruptedException e) {
                    throwable = e;
                }
            }

            @Override
            public void failed(Throwable t, Void v) {
                throwable = t;
            }

        });
    }

    public void write(ByteBuffer message, Runnable sentHandler, Consumer<Throwable> failedHandler) {
        chnl.write(message, SENDING_TIMEOUT, TimeUnit.SECONDS, message, new CompletionHandler<Integer, ByteBuffer>() {

            @Override
            public void completed(Integer bytesWritten, ByteBuffer msg) {
                if (bytesWritten < 0) {
                    failedHandler.accept(null);
                }
                if (msg.hasRemaining()) {
                    chnl.write(msg, null, this);
                    return;
                }
                sentHandler.run();
            }

            @Override
            public void failed(Throwable arg0, ByteBuffer arg1) {
                failedHandler.accept(arg0);
            }
        });
    }

    public void read(Consumer<ByteBuffer> reader, Consumer<Throwable> failedHandler) {
        if (throwable != null) {
            failedHandler.accept(throwable);
        }
        for (ByteBuffer message = messages.poll(); message != null; message = messages.poll()) {
            reader.accept(message);
            buffers.offer(message);
        }
    }

    public void close() throws IOException {
        chnl.close();
        throwable = null;
    }

}
