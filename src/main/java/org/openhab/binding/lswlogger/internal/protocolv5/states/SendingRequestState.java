/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.lswlogger.internal.protocolv5.states;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

import org.openhab.binding.lswlogger.internal.LoggerThingConfiguration;
import org.openhab.binding.lswlogger.internal.connection.Context;
import org.openhab.binding.lswlogger.internal.connection.LoggerConnectionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendingRequestState implements LoggerConnectionState {

    private static final Logger logger = LoggerFactory.getLogger(SendingRequestState.class);
    private static final int SENDING_TIMEOUT = 10;

    private final AsynchronousSocketChannel channel;

    public SendingRequestState(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void tick(Context context, LoggerThingConfiguration configuration) {
        ByteBuffer request = context.request();
        channel.write(request, SENDING_TIMEOUT, TimeUnit.SECONDS, request, new SendingHandler(context));
    }

    @Override
    public void close() {
        try {
            channel.close();
        } catch (IOException e) {
            logger.error("Cannot disconnect from channel", e);
        }
    }

    private class SendingHandler implements CompletionHandler<Integer, ByteBuffer> {

        private final Context context;

        public SendingHandler(Context context) {
            this.context = context;
        }

        @Override
        public void completed(Integer bytesWritten, ByteBuffer request) {
            if (bytesWritten < 0) {
                logger.error("Write problem, remain bytes count to be sent {}", request.remaining());
                context.switchTo(new ReconnectingState(channel, 0));
                return;
            }
            if (request.hasRemaining()) {
                channel.write(request, null, this);
                return;
            }
            context.switchTo(new ReadingResponseState(channel));
        }

        @Override
        public void failed(Throwable t, ByteBuffer unused) {
            logger.error("Failed to write to channel", t);
            context.switchTo(new ReconnectingState(channel, 0));
        }
    }
}
