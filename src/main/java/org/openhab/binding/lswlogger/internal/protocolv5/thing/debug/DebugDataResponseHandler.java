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

import org.openhab.binding.lswlogger.internal.ExtractingUtils;
import org.openhab.binding.lswlogger.internal.protocolv5.AbstractDataResponseHandler;
import org.openhab.binding.lswlogger.internal.protocolv5.ExtractorsBuilder;

public class DebugDataResponseHandler extends AbstractDataResponseHandler {

    public DebugDataResponseHandler(ExtractorsBuilder.ChannelStateUpdate stateUpdate) {
        super(new ExtractorsBuilder(stateUpdate)
                .add(lastResponseChannel, ExtractingUtils.bytesToString()));
    }

    @Override
    protected boolean accepts(ByteBuffer buffer) {
        return true;
    }
}
