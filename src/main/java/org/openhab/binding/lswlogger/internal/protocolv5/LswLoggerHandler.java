/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
 * <p>
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.lswlogger.internal.protocolv5;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.Power;

import org.openhab.binding.lswlogger.internal.LoggerThingConfiguration;
import org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants;
import org.openhab.binding.lswlogger.internal.connection.Context;
import org.openhab.binding.lswlogger.internal.connection.LoggerConnectionState;
import org.openhab.binding.lswlogger.internal.protocolv5.states.InitialState;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.unit.Units;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link LswLoggerHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Piotr Bojko - Initial contribution
 */

public class LswLoggerHandler extends BaseThingHandler {

    private static final Logger logger = LoggerFactory.getLogger(LswLoggerHandler.class);
    public static final QuantityType<Power> ZERO_VOLTS = new QuantityType<>(0, Units.WATT);
    public static final QuantityType<ElectricCurrent> ZERO_AMPERES = new QuantityType<>(0, Units.AMPERE);
    public static final QuantityType<Power> ZERO_WATTS = new QuantityType<>(0, Units.WATT);

    private final ResponseDispatcher responseDispatcher;
    private final ByteBuffer response = ByteBuffer.allocate(1024);
    private LswLoggerHandlerContext context;
    private LoggerThingConfiguration configuration;
    private ByteBuffer request;

    public LswLoggerHandler(Thing thing) {
        super(thing);
        responseDispatcher = ResponseDispatcher.create(new DataResponseHandler(this::updateState),
                new ExtendedDataResponseHandler(this::updateState),
                new UnknownResponseHandler(getThing().getUID().getId()));
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.UNKNOWN);
        disconnectWhenNeeded();
        context = new LswLoggerHandlerContext();
        configuration = this.getConfigAs(LoggerThingConfiguration.class);
        request = RequestFactory.create(configuration.getSerialNumber());
        request.flip();
        context.switchTo(new InitialState());
    }

    private void disconnectWhenNeeded() {
        if (context != null) {
            context.close();
        }
    }

    @Override
    public void handleRemoval() {
        super.handleRemoval();
        disconnectWhenNeeded();
    }

    @Override
    public void dispose() {
        super.dispose();
        disconnectWhenNeeded();
    }

    private class LswLoggerHandlerContext implements Context {

        private LoggerConnectionState state;
        private boolean closed = false;

        @Override
        public void switchTo(LoggerConnectionState state) {
            if (closed) {
                logger.warn("Context is closed, not switching to {}", state);
                return;
            }
            this.state = state;
            logger.debug("Ticking {}", state);
            state.tick(this, configuration);
        }

        @Override
        public ByteBuffer response() {
            response.clear();
            return response;
        }

        @Override
        public ByteBuffer request() {
            request.clear();
            return request;
        }

        @Override
        public void handleResponse() {
            updateStatus(ThingStatus.ONLINE);
            updateState(LswLoggerBindingConstants.LSWLoggerV5.onlineChannel, OnOffType.ON);
            response.flip();
            responseDispatcher.accept(response);
        }

        @Override
        public void schedule(int i, TimeUnit unit, Runnable runnable) {
            scheduler.schedule(runnable, i, unit);
        }

        @Override
        public void notifyLoggerIsOffline() {
            updateState(LswLoggerBindingConstants.LSWLoggerV5.onlineChannel, OnOffType.OFF);
            updateState(LswLoggerBindingConstants.LSWLoggerV5.gridAVoltageChannel, ZERO_VOLTS);
            updateState(LswLoggerBindingConstants.LSWLoggerV5.gridACurrentChannel, ZERO_AMPERES);
            updateState(LswLoggerBindingConstants.LSWLoggerV5.gridAPowerChannel, ZERO_WATTS);
            updateState(LswLoggerBindingConstants.LSWLoggerV5.gridBVoltageChannel, ZERO_VOLTS);
            updateState(LswLoggerBindingConstants.LSWLoggerV5.gridBCurrentChannel, ZERO_AMPERES);
            updateState(LswLoggerBindingConstants.LSWLoggerV5.gridBPowerChannel, ZERO_WATTS);
            updateState(LswLoggerBindingConstants.LSWLoggerV5.outputActivePowerChannel, ZERO_WATTS);
            updateState(LswLoggerBindingConstants.LSWLoggerV5.outputReactivePowerChannel, ZERO_WATTS);
            updateState(LswLoggerBindingConstants.LSWLoggerV5.Phase1CurrentChannel, ZERO_AMPERES);
            updateState(LswLoggerBindingConstants.LSWLoggerV5.Phase2CurrentChannel, ZERO_AMPERES);
            updateState(LswLoggerBindingConstants.LSWLoggerV5.Phase3CurrentChannel, ZERO_AMPERES);
        }

        @Override
        public void notifyCannotRecover() {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
        }

        public void close() {
            closed = true;
            if (state != null) {
                state.close();
            }
        }
    }
}
