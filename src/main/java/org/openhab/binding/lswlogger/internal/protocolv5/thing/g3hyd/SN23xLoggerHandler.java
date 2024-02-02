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
package org.openhab.binding.lswlogger.internal.protocolv5.thing.g3hyd;

import java.util.List;

import org.openhab.binding.lswlogger.internal.ChannelTypes;
import org.openhab.binding.lswlogger.internal.bytebuffer.ExtractingBuilder;
import org.openhab.binding.lswlogger.internal.bytebuffer.Extractors;
import org.openhab.binding.lswlogger.internal.protocolv5.modbus.AbstractModbusLoggerHandler;
import org.openhab.binding.lswlogger.internal.protocolv5.modbus.ModbusRegisterDefinitionBuilder;
import org.openhab.binding.lswlogger.internal.protocolv5.modbus.ModbusRegisterDefinitionBuilder.ModbusRegistryDefnition;
import org.openhab.core.library.unit.SIUnits;
import org.openhab.core.thing.Thing;

/**
 * The {@link SN23xLoggerHandler} is responsible for handling commands, which
 * are
 * sent to one of the channels.
 *
 * @author Piotr Bojko - Initial contribution
 */
public class SN23xLoggerHandler extends AbstractModbusLoggerHandler {

    private final String[] OPERATING_STATES = new String[] { "waiting", "detection", "grid-connected",
            "emergency-power-supply", "recoverable-fault", "permanent-fault", "upgrade", "self-charging" };

    public SN23xLoggerHandler(Thing thing) {
        super(thing);
    }

    @Override
    protected List<ModbusRegistryDefnition> createDefinitions() {
        return List.of(create0x0404To0x042EDefinition(), create0x0480To0x04AFDefinition(),
                create0x0684To0x069BDefinition());
    }

    private ModbusRegistryDefnition create0x0404To0x042EDefinition() {
        return new ModbusRegisterDefinitionBuilder(0x0404, 0x0422, this::updateState)
                .setFirstBytesEater(28)
                .add(0x0404, "operatingState", "Operating state", ChannelTypes.OPERATING_STATE,
                        ExtractingBuilder.extactShort().mappedTo(OPERATING_STATES))
                .add(0x0418, "ambientTemperature1", "Ambient temperature 1",
                        ChannelTypes.INTERNAL_TEMPERATURE,
                        ExtractingBuilder.extactShort().as(SIUnits.CELSIUS))
                .add(0x0419, "ambientTemperature2", "Ambient temperature 2",
                        ChannelTypes.INTERNAL_TEMPERATURE,
                        ExtractingBuilder.extactShort().as(SIUnits.CELSIUS))
                .add(0x041A, "radiatorTemperature1", "Radiator temperature 1",
                        ChannelTypes.INTERNAL_TEMPERATURE,
                        ExtractingBuilder.extactShort().as(SIUnits.CELSIUS))
                .add(0x041B, "radiatorTemperature3", "Radiator temperature 3",
                        ChannelTypes.INTERNAL_TEMPERATURE,
                        ExtractingBuilder.extactShort().as(SIUnits.CELSIUS))
                .add(0x041D, "radiatorTemperature4", "Radiator temperature 4",
                        ChannelTypes.INTERNAL_TEMPERATURE,
                        ExtractingBuilder.extactShort().as(SIUnits.CELSIUS))
                .add(0x041E, "radiatorTemperature5", "Radiator temperature 5",
                        ChannelTypes.INTERNAL_TEMPERATURE,
                        ExtractingBuilder.extactShort().as(SIUnits.CELSIUS))
                .add(0x041F, "radiatorTemperature6", "Radiator temperature 6",
                        ChannelTypes.INTERNAL_TEMPERATURE,
                        ExtractingBuilder.extactShort().as(SIUnits.CELSIUS))
                .add(0x0420, "moduleTemperature1", "Module temperature 1",
                        ChannelTypes.INTERNAL_TEMPERATURE,
                        ExtractingBuilder.extactShort().as(SIUnits.CELSIUS))
                .add(0x0421, "moduleTemperature2", "Module temperature 2",
                        ChannelTypes.INTERNAL_TEMPERATURE,
                        ExtractingBuilder.extactShort().as(SIUnits.CELSIUS))
                .add(0x0422, "moduleTemperature3", "Module temperature 3",
                        ChannelTypes.INTERNAL_TEMPERATURE,
                        ExtractingBuilder.extactShort().as(SIUnits.CELSIUS))
                .build();
    }

    private ModbusRegistryDefnition create0x0480To0x04AFDefinition() {
        return new ModbusRegisterDefinitionBuilder(0x0480, 0x04BC, this::updateState)
                .setFirstBytesEater(28)
                .add(0x0484, "gridFrequency", "Grid frequency", ChannelTypes.AC_FREQUENCY,
                        Extractors.HUNDRETHS_OF_HERZ)
                .add(0x0485, "totalOutputActivePower", "Total output active power",
                        ChannelTypes.ELECTRIC_POWER,
                        Extractors.SHORT_AS_DOZENS_WATT)
                .add(0x0486, "totalOutputReactivePower", "Total output reactive power",
                        ChannelTypes.ELECTRIC_POWER,
                        Extractors.SHORT_AS_DOZENS_WATT)
                .add(0x0487, "totalOutputApparentPower", "Total output apparent power",
                        ChannelTypes.ELECTRIC_POWER,
                        Extractors.SHORT_AS_DOZENS_WATT)
                .add(0x0488, "totalPCCActivePower", "Total PCC active power",
                        ChannelTypes.ELECTRIC_POWER,
                        Extractors.SHORT_AS_DOZENS_WATT)
                .add(0x0489, "totalPCCReactivePower", "Total PCC reactive power",
                        ChannelTypes.ELECTRIC_POWER,
                        Extractors.SHORT_AS_DOZENS_WATT)
                .add(0x048A, "totalPCCApparentPower", "Total PCC apparent power",
                        ChannelTypes.ELECTRIC_POWER,
                        Extractors.SHORT_AS_DOZENS_WATT)

                .add(0x048D, "phaseRVoltage", "Phase R voltage", ChannelTypes.ELECTRIC_VOLTAGE,
                        Extractors.SHORT_AS_TEHNTS_OF_VOLT)
                .add(0x048E, "phaseRCurrent", "Phase R current", ChannelTypes.ELECTRIC_CURRENT,
                        Extractors.SHORT_AS_HUNDRETHS_OF_AMPERE)
                .add(0x048F, "phaseRActivePower", "Phase R active power", ChannelTypes.ELECTRIC_POWER,
                        Extractors.SHORT_AS_DOZENS_WATT)
                .add(0x0490, "phaseRReactivePower", "Phase R reactive power",
                        ChannelTypes.ELECTRIC_POWER,
                        Extractors.SHORT_AS_DOZENS_WATT)
                .add(0x0491, "phaseRPowerFactor", "Phase R power factor", ChannelTypes.FACTOR,
                        Extractors.THOUSANDS_AS_FACTOR)
                .add(0x0492, "phaseRPCCCurrent", "Phase R PCC current", ChannelTypes.ELECTRIC_CURRENT,
                        Extractors.SHORT_AS_HUNDRETHS_OF_AMPERE)
                .add(0x0493, "phaseRPCCActivePower", "Phase R PCC active power",
                        ChannelTypes.ELECTRIC_POWER,
                        Extractors.SHORT_AS_DOZENS_WATT)
                .add(0x0494, "phaseRPCCReactivePower", "Phase R PCC reactive power",
                        ChannelTypes.ELECTRIC_POWER,
                        Extractors.SHORT_AS_DOZENS_WATT)
                .add(0x0495, "phaseRPCCPowerFactor", "Phase R PCC power factor", ChannelTypes.FACTOR,
                        Extractors.THOUSANDS_AS_FACTOR)

                .add(0x0498, "phaseSVoltage", "Phase S voltage", ChannelTypes.ELECTRIC_VOLTAGE,
                        Extractors.SHORT_AS_TEHNTS_OF_VOLT)
                .add(0x0499, "phaseSCurrent", "Phase S current", ChannelTypes.ELECTRIC_CURRENT,
                        Extractors.SHORT_AS_HUNDRETHS_OF_AMPERE)
                .add(0x049A, "phaseSActivePower", "Phase S active power", ChannelTypes.ELECTRIC_POWER,
                        Extractors.SHORT_AS_DOZENS_WATT)
                .add(0x049B, "phaseSReactivePower", "Phase S reactive power",
                        ChannelTypes.ELECTRIC_POWER,
                        Extractors.SHORT_AS_DOZENS_WATT)
                .add(0x049C, "phaseSPowerFactor", "Phase S power factor", ChannelTypes.FACTOR,
                        Extractors.THOUSANDS_AS_FACTOR)
                .add(0x049D, "phaseSPCCCurrent", "Phase S PCC current", ChannelTypes.ELECTRIC_CURRENT,
                        Extractors.SHORT_AS_HUNDRETHS_OF_AMPERE)
                .add(0x049E, "phaseSPCCActivePower", "Phase S PCC active power",
                        ChannelTypes.ELECTRIC_POWER,
                        Extractors.SHORT_AS_DOZENS_WATT)
                .add(0x049F, "phaseSPCCReactivePower", "Phase S PCC reactive power",
                        ChannelTypes.ELECTRIC_POWER,
                        Extractors.SHORT_AS_DOZENS_WATT)
                .add(0x04A0, "phaseSPCCPowerFactor", "Phase S PCC power factor", ChannelTypes.FACTOR,
                        Extractors.THOUSANDS_AS_FACTOR)

                .add(0x04A3, "phaseTVoltage", "Phase T voltage", ChannelTypes.ELECTRIC_VOLTAGE,
                        Extractors.SHORT_AS_TEHNTS_OF_VOLT)
                .add(0x04A4, "phaseTCurrent", "Phase T current", ChannelTypes.ELECTRIC_CURRENT,
                        Extractors.SHORT_AS_HUNDRETHS_OF_AMPERE)
                .add(0x04A5, "phaseTActivePower", "Phase T active power", ChannelTypes.ELECTRIC_POWER,
                        Extractors.SHORT_AS_DOZENS_WATT)
                .add(0x04A6, "phaseTReactivePower", "Phase T reactive power",
                        ChannelTypes.ELECTRIC_POWER,
                        Extractors.SHORT_AS_DOZENS_WATT)
                .add(0x04A7, "phaseTPowerFactor", "Phase T power factor", ChannelTypes.FACTOR,
                        Extractors.THOUSANDS_AS_FACTOR)
                .add(0x04A8, "phaseTPCCCurrent", "Phase T PCC current", ChannelTypes.ELECTRIC_CURRENT,
                        Extractors.SHORT_AS_HUNDRETHS_OF_AMPERE)
                .add(0x04A9, "phaseTPCCActivePower", "Phase T PCC active power",
                        ChannelTypes.ELECTRIC_POWER,
                        Extractors.SHORT_AS_DOZENS_WATT)
                .add(0x04AA, "phaseTPCCReactivePower", "Phase T PCC reactive power",
                        ChannelTypes.ELECTRIC_POWER,
                        Extractors.SHORT_AS_DOZENS_WATT)
                .add(0x04AB, "phaseTPCCPowerFactor", "Phase T PCC power factor", ChannelTypes.FACTOR,
                        Extractors.THOUSANDS_AS_FACTOR)

                .add(0x04AE, "pvExtActivePower", "PV external active power",
                        ChannelTypes.ELECTRICAL_ENERGY, Extractors.SHORT_AS_DOZENS_WATT)
                .add(0x04AF, " loadSysActivePower", "Load sys active power", ChannelTypes.ELECTRICAL_ENERGY,
                        Extractors.SHORT_AS_DOZENS_WATT)

                .build();
    }

    private ModbusRegistryDefnition create0x0684To0x069BDefinition() {
        return new ModbusRegisterDefinitionBuilder(0x0680, 0x069B, this::updateState)
                .setFirstBytesEater(28)
                .add(0x0684, "todayEnergyProduction", "Daily PV Generation", ChannelTypes.ELECTRICAL_ENERGY,
                        Extractors.INT_AS_HUNDRETHS_OF_KILOWATT_HOUR)
                .add(0x0686, "totalEnergyProduction", "Total PV Generation", ChannelTypes.ELECTRICAL_ENERGY,
                        Extractors.INT_AS_TENTHS_OF_KILOWATT_HOUR)
                .add(0x0688, "todayEnergyConsumption", "Daily Load Consumption", ChannelTypes.ELECTRICAL_ENERGY,
                        Extractors.INT_AS_HUNDRETHS_OF_KILOWATT_HOUR)
                .add(0x068A, "totalEnergyConsumption", "Total Load Consumption", ChannelTypes.ELECTRICAL_ENERGY,
                        Extractors.INT_AS_TENTHS_OF_KILOWATT_HOUR)
                .add(0x068C, "todayEnergyPurchased", "Daily Energy Bought", ChannelTypes.ELECTRICAL_ENERGY,
                        Extractors.INT_AS_HUNDRETHS_OF_KILOWATT_HOUR)
                .add(0x068E, "totalEnergyPurchased", "Total Energy Bought", ChannelTypes.ELECTRICAL_ENERGY,
                        Extractors.INT_AS_TENTHS_OF_KILOWATT_HOUR)
                .add(0x0690, "todayEnergySold", "Daily Energy Sold", ChannelTypes.ELECTRICAL_ENERGY,
                        Extractors.INT_AS_HUNDRETHS_OF_KILOWATT_HOUR)
                .add(0x0692, "totalEnergySold", "Total Energy Sold", ChannelTypes.ELECTRICAL_ENERGY,
                        Extractors.INT_AS_TENTHS_OF_KILOWATT_HOUR)
                .add(0x0694, "todayBatteryCharged", "Daily Battery Charge", ChannelTypes.ELECTRICAL_ENERGY,
                        Extractors.INT_AS_HUNDRETHS_OF_KILOWATT_HOUR)
                .add(0x0696, "totalBatteryCharged", "Total Battery Charge", ChannelTypes.ELECTRICAL_ENERGY,
                        Extractors.INT_AS_TENTHS_OF_KILOWATT_HOUR)
                .add(0x0698, "todayBatteryDischarged", "Daily Battery Discharge", ChannelTypes.ELECTRICAL_ENERGY,
                        Extractors.INT_AS_HUNDRETHS_OF_KILOWATT_HOUR)
                .add(0x069A, "totalBatteryDischarged", "Total Battery Discharge", ChannelTypes.ELECTRICAL_ENERGY,
                        Extractors.INT_AS_TENTHS_OF_KILOWATT_HOUR)
                .build();
    }
}
