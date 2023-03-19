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
package org.openhab.binding.lswlogger.internal;

import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.Power;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.unit.Units;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link LswLoggerBindingConstants} class defines common constants, which
 * are
 * used across the whole binding.
 *
 * @author Piotr Bojko - Initial contribution
 */
@NonNullByDefault
public interface LswLoggerBindingConstants {

    String BINDING_ID = "lswlogger";

    interface InitialDataValues {

        QuantityType<Power> ZERO_VOLTS = new QuantityType<>(0, Units.WATT);
        QuantityType<ElectricCurrent> ZERO_AMPERES = new QuantityType<>(0, Units.AMPERE);
        QuantityType<Power> ZERO_WATTS = new QuantityType<>(0, Units.WATT);

    }

    interface Common {

        String onlineChannel = "online";
    }

    interface LSWLoggerV5 {
        ThingTypeUID THING_TYPE_ID = new ThingTypeUID(BINDING_ID, "LSWLoggerV5");
        String[] OPERATING_STATES = new String[] { "Unknown", "Check", "Normal", "Fault", "Permanent" };
        String operatingStateChannel = "operatingState";
        String faultChannel = "fault";
        String gridAVoltageChannel = "gridAVoltage";
        String gridACurrentChannel = "gridACurrent";
        String gridBVoltageChannel = "gridBVoltage";
        String gridBCurrentChannel = "gridBCurrent";
        String gridAPowerChannel = "gridAPower";
        String gridBPowerChannel = "gridBPower";
        String outputActivePowerChannel = "outputActivePower";
        String outputReactivePowerChannel = "outputReactivePower";
        String outputFrequencyChannel = "outputFrequency";
        String Phase1VoltageChannel = "1stPhaseVoltage";
        String Phase1CurrentChannel = "1stPhaseCurrent";
        String Phase2VoltageChannel = "2ndPhaseVoltage";
        String Phase2CurrentChannel = "2ndPhaseCurrent";
        String Phase3VoltageChannel = "3rdPhaseVoltage";
        String Phase3CurrentChannel = "3rdPhaseCurrent";
        String totalEnergyProductionChannel = "totalEnergyProduction";
        String totalGenerationTimeChannel = "totalGenerationTime";
        String todayEnergyProductionChannel = "todayEnergyProduction";
        String todayGenerationTimeChannel = "todayGenerationTime";
        String inverterModuleTemperatureChannel = "inverterModuleTemperature";
        String inverterInnerTemperatureChannel = "inverterInnerTemperature";
    }

    interface SN23xV5 {
        ThingTypeUID THING_TYPE_ID = new ThingTypeUID(BINDING_ID, "SN23xLoggerV5");
        String[] OPERATING_STATES = new String[] { "waiting", "detection", "grid-connected", "emergency-power-supply",
                "recoverable-fault", "permanent-fault", "upgrade", "self-charging" };

        interface From0404To042eChannels {
            String operatingStateChannel = "operatingState";
            String faultChannel = "fault";
            String countdown = "countdown";
            String env1Temperature = "env1Temperature";
            String env2Temperature = "env2Temperature";
            String heatSink1Temperature = "heatSink1Temperature";
            String heatSink2Temperature = "heatSink2Temperature";
            String heatSink3Temperature = "heatSink3Temperature";
            String heatSink4Temperature = "heatSink4Temperature";
            String heatSink5Temperature = "heatSink5Temperature";
            String heatSink6Temperature = "heatSink6Temperature";
            String inv1Temperature = "inv1Temperature";
            String inv2Temperature = "inv2Temperature";
            String inv3Temperature = "inv3Temperature";
            String rsvd1Temperature = "rsvd1Temperature";
            String rsvd2Temperature = "rsvd2Temperature";
            String rsvd3Temperature = "rsvd3Temperature";
            String todayGenerationTime = "todayGenerationTime";
            String totalGenerationTime = "totalGenerationTime";
            String totalServiceTime = "totalServiceTime";
        }

        interface From0480To04afChannels {
            String gridFrequency = "gridFrequency";
            String totalOutputActivePower = "outputActivePower";
            String totalOutputReactivePower = "totalOutputReactivePower";
            String totalOutputApparentPower = "totalOutputApparentPower";
            String totalPCCActivePower = "totalPCCActivePower";
            String totalPCCReactivePower = "totalPCCReactivePower";
            String totalPCCApparentPower = "totalPCCApparentPower";

            String phaseRVoltage = "phaseRVoltage";
            String phaseRCurrent = "phaseRCurrent";
            String phaseRActivePower = "phaseRActivePower";
            String phaseRReactivePower = "phaseRReactivePower";
            String phaseRPowerFactor = "phaseRPowerFactor";
            String phaseRPCCCurrent = "phaseRPCCCurrent";
            String phaseRPCCActivePower = "phaseRPCCActivePower";
            String phaseRPCCReactivePower = "phaseRPCCReactivePower";
            String phaseRPCCPowerFactor = "phaseRPCCPowerFactor";

            String phaseSVoltage = "phaseSVoltage";
            String phaseSCurrent = "phaseSCurrent";
            String phaseSActivePower = "phaseSActivePower";
            String phaseSReactivePower = "phaseSReactivePower";
            String phaseSPowerFactor = "phaseSPowerFactor";
            String phaseSPCCCurrent = "phaseSPCCCurrent";
            String phaseSPCCActivePower = "phaseSPCCActivePower";
            String phaseSPCCReactivePower = "phaseSPCCReactivePower";
            String phaseSPCCPowerFactor = "phaseSPCCPowerFactor";

            String phaseTVoltage = "phaseTVoltage";
            String phaseTCurrent = "phaseTCurrent";
            String phaseTActivePower = "phaseTActivePower";
            String phaseTReactivePower = "phaseTReactivePower";
            String phaseTPowerFactor = "phaseTPowerFactor";
            String phaseTPCCCurrent = "phaseTPCCCurrent";
            String phaseTPCCActivePower = "phaseTPCCActivePower";
            String phaseTPCCReactivePower = "phaseTPCCReactivePower";
            String phaseTPCCPowerFactor = "phaseTPCCPowerFactor";

            String pvExtActivePower = "pvExtActivePower";
            String loadSysActivePower = "loadSysActivePower";

            String phaseL1NVoltage = "phaseL1NVoltage";
            String phaseL1NCurrent = "phaseL1NCurrent";
            String phaseL1NActivePower = "phaseL1NActivePower";
            String phaseL1NPCCCurrent = "phaseL1NPCCCurrent";
            String phaseL1NPCCActivePower = "phaseL1NPCCActivePower";

            String phaseL2NVoltage = "phaseL2NVoltage";
            String phaseL2NCurrent = "phaseL2NCurrent";
            String phaseL2NActivePower = "phaseL2NActivePower";
            String phaseL2NPCCCurrent = "phaseL2NPCCCurrent";
            String phaseL2NPCCActivePower = "phaseL2NPCCActivePower";

            String lineL1Voltage = "lineL1Voltage";
            String lineL2Voltage = "lineL2Voltage";
            String lineL3Voltage = "lineL3Voltage";

        }

        interface From0680To069bChannels {
            String todayEnergyProduction = "todayEnergyProduction";
            String totalEnergyProduction = "totalEnergyProduction";
            String todayEnergyConsumption = "todayEnergyConsumption";
            String totalEnergyConsumption = "totalEnergyConsumption";
            String todayEnergyPurchased = "todayEnergyPurchased";
            String totalEnergyPurchased = "totalEnergyPurchased";
            String todayEnergySold = "todayEnergySold";
            String totalEnergySold = "totalEnergySold";
            String todayBatteryCharged = "todayBatteryCharged";
            String totalBatteryCharged = "totalBatteryCharged";
            String todayBatteryDischarged = "todayBatteryDischarged";
            String totalBatteryDischarged = "totalBatteryDischarged";
        }
    }
}
