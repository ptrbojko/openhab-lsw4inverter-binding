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
    }
}
