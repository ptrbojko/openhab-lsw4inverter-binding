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
package org.openhab.binding.lswlogger.internal.protocolv5.thing.debug;

import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.DebugLoggerV5.lastResponseChannel;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import org.openhab.binding.lswlogger.internal.bytebuffer.ExtractingUtils;
import org.openhab.binding.lswlogger.internal.protocolv5.AbstractDataResponseHandler;
import org.openhab.binding.lswlogger.internal.protocolv5.ChannelStateUpdate;
import org.openhab.binding.lswlogger.internal.protocolv5.SequenceExtractorsBuilder;

public class DebugDataResponseHandler extends AbstractDataResponseHandler {

    private Consumer<ByteBuffer> consumer;

    public DebugDataResponseHandler(ChannelStateUpdate stateUpdate) {
        consumer = new SequenceExtractorsBuilder(stateUpdate)
                .add(lastResponseChannel, ExtractingUtils.bytesToHex()).build();
    }

    @Override
    protected boolean accepts(ByteBuffer buffer) {
        return true;
    }

    @Override
    protected void accept(ByteBuffer byteBuffer) {
        consumer.accept(byteBuffer);
        ;
    }
}
