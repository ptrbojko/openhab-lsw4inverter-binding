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
package org.openhab.binding.lswlogger.internal.protocolv5.thing.lsw3;

import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.Phase1CurrentChannel;
import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.Phase1VoltageChannel;
import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.Phase2CurrentChannel;
import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.Phase2VoltageChannel;
import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.Phase3CurrentChannel;
import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.Phase3VoltageChannel;
import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.faultChannel;
import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.gridACurrentChannel;
import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.gridAPowerChannel;
import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.gridAVoltageChannel;
import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.gridBCurrentChannel;
import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.gridBPowerChannel;
import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.gridBVoltageChannel;
import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.inverterInnerTemperatureChannel;
import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.inverterModuleTemperatureChannel;
import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.operatingStateChannel;
import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.outputActivePowerChannel;
import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.outputFrequencyChannel;
import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.outputReactivePowerChannel;
import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.todayEnergyProductionChannel;
import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.todayGenerationTimeChannel;
import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.totalEnergyProductionChannel;
import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.totalGenerationTimeChannel;
import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.OPERATING_STATES;

import java.nio.ByteBuffer;

import org.openhab.binding.lswlogger.internal.ExtractingUtils;
import org.openhab.binding.lswlogger.internal.protocolv5.AbstractDataResponseHandler;
import org.openhab.binding.lswlogger.internal.protocolv5.ExtractorsBuilder;

public class DataResponseHandler extends AbstractDataResponseHandler {

    private static final int RESPONSE_LENGTH = 110;

    public DataResponseHandler(ExtractorsBuilder.ChannelStateUpdate stateUpdate) {
        super(new ExtractorsBuilder(stateUpdate).addBytesEater(28)
                .add(operatingStateChannel, v -> ExtractingUtils.extractShortToToStringType(v, OPERATING_STATES))
                .add(faultChannel, ExtractingUtils.bytesToString(10))
                .add(gridAVoltageChannel, ExtractingUtils::shortToTenthsAsVoltage)
                .add(gridACurrentChannel, ExtractingUtils::shortToHundrethsAsAmpers)
                .add(gridBVoltageChannel, ExtractingUtils::shortToTenthsAsVoltage)
                .add(gridBCurrentChannel, ExtractingUtils::shortToHundrethsAsAmpers)
                .add(gridAPowerChannel, ExtractingUtils::shortMultiple10AsWatts)
                .add(gridBPowerChannel, ExtractingUtils::shortMultiple10AsWatts)
                .add(outputActivePowerChannel, ExtractingUtils::shortMultiple10AsWatts)
                .add(outputReactivePowerChannel, ExtractingUtils::shortMultiple10AsVars)
                .add(outputFrequencyChannel, ExtractingUtils::shortToHundrethsAsHz)
                .add(Phase1VoltageChannel, ExtractingUtils::shortToTenthsAsVoltage)
                .add(Phase1CurrentChannel, ExtractingUtils::shortToHundrethsAsAmpers)
                .add(Phase2VoltageChannel, ExtractingUtils::shortToTenthsAsVoltage)
                .add(Phase2CurrentChannel, ExtractingUtils::shortToHundrethsAsAmpers)
                .add(Phase3VoltageChannel, ExtractingUtils::shortToTenthsAsVoltage)
                .add(Phase3CurrentChannel, ExtractingUtils::shortToHundrethsAsAmpers)
                .add(totalEnergyProductionChannel, ExtractingUtils::intToDecimalAsKiloWattHour)
                .add(totalGenerationTimeChannel, ExtractingUtils::intToHours)
                .add(todayEnergyProductionChannel, ExtractingUtils::shortToHundrethsAsKiloWattHour)
                .add(todayGenerationTimeChannel, ExtractingUtils::shortAsMinute)
                .add(inverterModuleTemperatureChannel, ExtractingUtils::shortToTemperature)
                .add(inverterInnerTemperatureChannel, ExtractingUtils::shortToTemperature));
    }

    @Override
    protected boolean accepts(ByteBuffer buffer) {
        return buffer.remaining() == RESPONSE_LENGTH;
    }
}
