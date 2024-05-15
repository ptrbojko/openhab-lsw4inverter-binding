package org.openhab.binding.lswlogger.internal.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Channel {

    private static final Logger logger = LoggerFactory.getLogger(Channel.class);

    private static final int SENDING_TIMEOUT = 10;

    private final ByteBuffer buffer = ByteBuffer.allocate(1024);
    private AsynchronousSocketChannel asyncChannel;
    private ChannelReader reader = new PassErrorChannelReader();

    public synchronized void reopen(InetSocketAddress address, Runnable connectedHandler,
            Consumer<Throwable> failedOpenHandler, Consumer<Throwable> failedConnectHandler) {
        try {
            if (asyncChannel != null && asyncChannel.isOpen()) {
                asyncChannel.close();
            }
            asyncChannel = AsynchronousSocketChannel.open();
            asyncChannel.connect(address, null, new CompletionHandler<Void, Void>() {

                @Override
                public void completed(Void v1, Void v2) {
                    logger.info("Connected to inverter/logger");
                    connectedHandler.run();
                    listen();
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
        logger.debug("Listening for new message from logger");
        buffer.clear();
        asyncChannel.read(buffer, null, new CompletionHandler<Integer, Void>() {

            @Override
            public void completed(Integer bytesRead, Void attachment) {
                logger.debug("Received {} bytes", bytesRead);
                buffer.flip();
                if (bytesRead < 0) {
                    reader.onFail(new IllegalStateException("Channel reads -1 bytes. Failing channel"));
                    return;
                }
                if (!asyncChannel.isOpen()) {
                    logger.warn("Channel closed");
                    reader.onFail(new IllegalStateException("Channel closed. Failing channel."));
                    return;
                }
                reader.onRead(buffer);
                listen();
            }

            @Override
            public void failed(Throwable t, Void v) {
                reader.onFail(t);
            }

        });
    }

    public void write(ByteBuffer message, Runnable sentHandler, Consumer<Throwable> failedHandler) {
        asyncChannel.write(message, SENDING_TIMEOUT, TimeUnit.SECONDS, message,
                new CompletionHandler<Integer, ByteBuffer>() {

                    @Override
                    public void completed(Integer bytesWritten, ByteBuffer msg) {
                        if (bytesWritten < 0) {
                            failedHandler.accept(null);
                        }
                        if (msg.hasRemaining()) {
                            asyncChannel.write(msg, null, this);
                            return;
                        }
                        sentHandler.run();
                    }

                    @Override
                    public void failed(Throwable error, ByteBuffer abufferg1) {
                        failedHandler.accept(error);
                    }
                });
    }

    public void startReading(ChannelReader reader) {
        this.reader.transfer(reader);
        this.reader = reader;
    }

    public void close() throws IOException {
        stopReading();
        asyncChannel.close();
    }

    public void stopReading() {
        logger.debug("Detaching channel reader");
        ChannelReader reader = new PassErrorChannelReader();
        this.reader.transfer(reader);
        this.reader = reader;
    }

    public interface ChannelReader {
        void onRead(ByteBuffer buffer);

        void onFail(Throwable throwable);

        void transfer(ChannelReader reader);
    }

    private final static class PassErrorChannelReader implements ChannelReader {

        private Throwable error;

        @Override
        public void onRead(ByteBuffer buffer) {
            logger.warn("Forgetting message, length {}", buffer.remaining());
        }

        @Override
        public void onFail(Throwable throwable) {
            this.error = throwable;
        }

        @Override
        public void transfer(ChannelReader reader) {
            if (error != null) {
                reader.onFail(error);
            }
            ;
        }

    }

}
