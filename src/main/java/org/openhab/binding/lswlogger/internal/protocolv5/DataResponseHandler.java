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

import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5.*;

import org.openhab.binding.lswlogger.internal.ExtractingUtils;

public class DataResponseHandler extends AbstractDataResponseHandler {

    private static final int RESPONSE_LENGTH = 110;

    public DataResponseHandler(ExtractorsBuilder.ChannelStateUpdate stateUpdate) {
        super(new ExtractorsBuilder(stateUpdate).addBytesEater(28)
                .add(operatingStateChannel, ExtractingUtils::extractShortToToStringType)
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
                .add(totalEnergyProductionChannel, ExtractingUtils::extractIntToDecimalAsKiloWattHour)
                .add(totalGenerationTimeChannel, ExtractingUtils::extractIntToHours)
                .add(todayEnergyProductionChannel, ExtractingUtils::shortToHundrethsAsKiloWattHour)
                .add(todayGenerationTimeChannel, ExtractingUtils::extractShortAsMinute)
                .add(inverterModuleTemperatureChannel, ExtractingUtils::extractShortToTemperature)
                .add(inverterInnerTemperatureChannel, ExtractingUtils::extractShortToTemperature));
    }

    @Override
    protected int getResponseLength() {
        return RESPONSE_LENGTH;
    }
}
