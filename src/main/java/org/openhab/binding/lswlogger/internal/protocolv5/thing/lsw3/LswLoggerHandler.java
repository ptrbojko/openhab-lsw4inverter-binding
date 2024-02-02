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
package org.openhab.binding.lswlogger.internal.protocolv5.thing.lsw3;

import java.util.List;

import org.openhab.binding.lswlogger.internal.ChannelTypes;
import org.openhab.binding.lswlogger.internal.bytebuffer.ExtractingBuilder;
import org.openhab.binding.lswlogger.internal.bytebuffer.Extractors;
import org.openhab.binding.lswlogger.internal.protocolv5.modbus.AbstractModbusLoggerHandler;
import org.openhab.binding.lswlogger.internal.protocolv5.modbus.ModbusRegisterDefinitionBuilder;
import org.openhab.binding.lswlogger.internal.protocolv5.modbus.ModbusRegisterDefinitionBuilder.ModbusRegistryDefnition;
import org.openhab.core.library.unit.SIUnits;
import org.openhab.core.library.unit.Units;
import org.openhab.core.thing.Thing;

/**
 * The {@link LswLoggerHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Piotr Bojko - Initial contribution
 */

public class LswLoggerHandler extends AbstractModbusLoggerHandler {

    private final static String[] OPERATING_STATES = { "Stand-by", "Self-checking", "Normal", "Fault",
            "Permanent" };

    private final static String[] COUNTRY_CODES = { "Germany", "CEI0-21 Internal", "Australia", "Spain RD1699",
            "Turkey", "Denmark", "Greece", "Netherlands", "Belgium", "UK-G59", "China", "France", "Poland",
            "Germany BDEW", "Germany VDE0126", "Italy CEI0-16", "UK-G83", "Greece Islands", "EU EN50438", "EU EN61727",
            "Korea", "Sweden", "Europe General", "CEI0-21 External", "Cyprus", "India", "Philippines", "New Zealand",
            "Reserve", "Reserve" };

    public LswLoggerHandler(Thing thing) {
        super(thing);
    }

    @Override
    protected List<ModbusRegistryDefnition> createDefinitions() {
        return List.of(create0x000To0x027Definition());
    }

    private ModbusRegistryDefnition create0x000To0x027Definition() {
        return new ModbusRegisterDefinitionBuilder(0x0000, 0x0027, this::updateState)
                .setFirstBytesEater(28)
                .add(0x000, "operatingState", "Operating state", ChannelTypes.OPERATING_STATE,
                        ExtractingBuilder.extactShort().mappedTo(OPERATING_STATES))
                // add .add(faultChannel, ExtractingUtils.bytesToString(10)), 0x0001..0x0005
                .add(0x0006, "gridAVoltage", "Grid/PV A Voltage", ChannelTypes.ELECTRIC_VOLTAGE,
                        Extractors.SHORT_AS_TEHNTS_OF_VOLT)
                .add(0x0007, "gridACurrent", "Grid/PV A Current", ChannelTypes.ELECTRIC_CURRENT,
                        Extractors.SHORT_AS_HUNDRETHS_OF_AMPERE)
                .add(0x0006, "gridBVoltage", "Grid/PV B Voltage", ChannelTypes.ELECTRIC_VOLTAGE,
                        Extractors.SHORT_AS_TEHNTS_OF_VOLT)
                .add(0x0007, "gridBCurrent", "Grid/PV B Current", ChannelTypes.ELECTRIC_CURRENT,
                        Extractors.SHORT_AS_HUNDRETHS_OF_AMPERE)
                .add(0x000A, "gridAPower", "Grid/PB A Power", ChannelTypes.ELECTRIC_POWER,
                        Extractors.SHORT_AS_DOZENS_WATT)
                .add(0x000B, "gridBPower", "Grid/PB B Power", ChannelTypes.ELECTRIC_POWER,
                        Extractors.SHORT_AS_DOZENS_WATT)
                .add(0x000C, "outputActivePower", "Output active power", ChannelTypes.ELECTRIC_POWER,
                        Extractors.SHORT_AS_DOZENS_WATT)
                .add(0x000D, "outputReactivePower", "Output reactive power", ChannelTypes.ELECTRIC_POWER,
                        Extractors.SHORT_AS_DOZENS_WATT)
                .add(0x000E, "outputFrequency", "Output frequency", ChannelTypes.AC_FREQUENCY,
                        Extractors.HUNDRETHS_OF_HERZ)
                .add(0x000F, "1stPhaseVoltage", "L1 voltage", ChannelTypes.ELECTRIC_VOLTAGE,
                        Extractors.SHORT_AS_TEHNTS_OF_VOLT)
                .add(0x0010, "1stPhaseCurrent", "L1 current", ChannelTypes.ELECTRIC_CURRENT,
                        Extractors.SHORT_AS_HUNDRETHS_OF_AMPERE)
                .add(0x0011, "2ndPhaseVoltage", "L2 voltage", ChannelTypes.ELECTRIC_VOLTAGE,
                        Extractors.SHORT_AS_TEHNTS_OF_VOLT)
                .add(0x0012, "2ndPhaseCurrent", "L2 current", ChannelTypes.ELECTRIC_CURRENT,
                        Extractors.SHORT_AS_HUNDRETHS_OF_AMPERE)
                .add(0x0013, "3rdPhaseVoltage", "L3 voltage", ChannelTypes.ELECTRIC_VOLTAGE,
                        Extractors.SHORT_AS_TEHNTS_OF_VOLT)
                .add(0x0014, "3rdPhaseCurrent", "L3 current", ChannelTypes.ELECTRIC_CURRENT,
                        Extractors.SHORT_AS_HUNDRETHS_OF_AMPERE)
                .add(0x0015, "totalEnergyProduction", "Total energy production", ChannelTypes.ELECTRICAL_ENERGY,
                        ExtractingBuilder.extractInteger().as(Units.KILOWATT_HOUR))
                .add(0x0017, "totalGenerationTime", "Total generation time", ChannelTypes.TIME_PERIOD,
                        ExtractingBuilder.extractInteger().as(Units.HOUR))
                .add(0x0019, "todayEnergyProduction", "Today energy production", ChannelTypes.ELECTRICAL_ENERGY,
                        ExtractingBuilder.extactShort().divided(100).as(Units.KILOWATT_HOUR))
                .add(0x001A, "todayGenerationTime", "Today generation time", ChannelTypes.TIME_PERIOD,
                        ExtractingBuilder.extactShort().as(Units.MINUTE))
                .add(0x001B, "inverterModuleTemperature", "Inverter module temperature",
                        ChannelTypes.INTERNAL_TEMPERATURE,
                        ExtractingBuilder.extactShort().as(SIUnits.CELSIUS))
                .add(0x001C, "inverterInnerTemperature", "Inverter inner temperature",
                        ChannelTypes.INTERNAL_TEMPERATURE,
                        ExtractingBuilder.extactShort().as(SIUnits.CELSIUS))
                .add(0x0027, "country", "Country code", ChannelTypes.COUNTRY_CODE,
                        ExtractingBuilder.extactShort().mappedTo(COUNTRY_CODES))
                .build();
    }

    // todo: Following should be zeroing when offline

    // @Override
    // public void notifyLoggerIsOffline() {
    // super.notifyLoggerIsOffline();
    // updateState(LSWLoggerV5.gridAVoltageChannel, InitialDataValues.ZERO_VOLTS);
    // updateState(LSWLoggerV5.gridACurrentChannel, InitialDataValues.ZERO_AMPERES);
    // updateState(LSWLoggerV5.gridAPowerChannel, InitialDataValues.ZERO_WATTS);
    // updateState(LSWLoggerV5.gridBVoltageChannel, InitialDataValues.ZERO_VOLTS);
    // updateState(LSWLoggerV5.gridBCurrentChannel, InitialDataValues.ZERO_AMPERES);
    // updateState(LSWLoggerV5.gridBPowerChannel, InitialDataValues.ZERO_WATTS);
    // updateState(LSWLoggerV5.outputActivePowerChannel,
    // InitialDataValues.ZERO_WATTS);
    // updateState(LSWLoggerV5.outputReactivePowerChannel,
    // InitialDataValues.ZERO_WATTS);
    // updateState(LSWLoggerV5.Phase1CurrentChannel,
    // InitialDataValues.ZERO_AMPERES);
    // updateState(LSWLoggerV5.Phase2CurrentChannel,
    // InitialDataValues.ZERO_AMPERES);
    // updateState(LSWLoggerV5.Phase3CurrentChannel,
    // InitialDataValues.ZERO_AMPERES);
    // }

}
