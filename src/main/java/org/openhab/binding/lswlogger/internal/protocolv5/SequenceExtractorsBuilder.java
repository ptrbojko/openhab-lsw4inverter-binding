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
package org.openhab.binding.lswlogger.internal.protocolv5;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.openhab.core.types.State;

public class SequenceExtractorsBuilder {

    private final ChannelStateUpdate stateUpdate;
    private final List<Consumer<ByteBuffer>> extractors = new ArrayList<>();

    public SequenceExtractorsBuilder(ChannelStateUpdate stateUpdate) {
        this.stateUpdate = stateUpdate;
    }

    public SequenceExtractorsBuilder add(String channelName, Function<ByteBuffer, State> extractor) {
        extractors.add(buffer -> stateUpdate.apply(channelName, extractor.apply(buffer)));
        return this;
    }

    public SequenceExtractorsBuilder addBytesEater(int count) {
        extractors.add(buffer -> buffer.position(count + buffer.position()));
        return this;
    }

    public Consumer<ByteBuffer> build() {
        return buffer -> extractors.forEach(e -> e.accept(buffer));
    }
}
