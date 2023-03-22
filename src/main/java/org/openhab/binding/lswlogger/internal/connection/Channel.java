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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Channel {

    private static final Logger logger = LoggerFactory.getLogger(Channel.class);

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
                    listen();
                    connectedHandler.run();
                }

                @Override
                public void failed(Throwable t, Void v) {
                    logger.error("Error during connecting", t);
                    failedConnectHandler.accept(t);
                }

            });
        } catch (Exception e) {
            logger.error("Error during reopen", e);
            failedOpenHandler.accept(e);
        }
    }

    private void listen() {
        logger.debug("Listening for data, buffers {} messages {}", buffers.size(), messages.size());
        ByteBuffer buffer = buffers.remove();
        buffer.clear();
        chnl.read(buffer, null, new CompletionHandler<Integer, Void>() {

            @Override
            public void completed(Integer bytesRead, Void attachment) {
                logger.debug("Received {} bytes", bytesRead);
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
                try {
                    buffers.put(buffer);
                } catch (InterruptedException e) {
                    logger.error("Abnormal case in channel wrapper", e);
                }
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

    public void read(Consumer<ByteBuffer> reader, Runnable noMessageHandler, Consumer<Throwable> failedHandler) {
        logger.debug("Consuming messages, buffers {} messages {}", buffers.size(), messages.size());
        if (throwable != null) {
            failedHandler.accept(throwable);
            return;
        }
        for (ByteBuffer message = messages.poll(); message != null; message = messages.poll()) {
            reader.accept(message);
            buffers.offer(message);
        }
        noMessageHandler.run();
    }

    public void close() throws IOException {
        chnl.close();
        throwable = null;
    }

}
