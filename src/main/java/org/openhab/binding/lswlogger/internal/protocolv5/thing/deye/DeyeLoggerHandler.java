package org.openhab.binding.lswlogger.internal.protocolv5.thing.deye;

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

public class DeyeLoggerHandler extends AbstractModbusLoggerHandler {

	private static final String[] OPERATING_STATES = new String[] { "Stand-by", "Self-check", "Normal", "Warning",
			"Fault" };

	public DeyeLoggerHandler(Thing thing) {
		super(thing);
	}

	@Override
	protected List<ModbusRegistryDefnition> createDefinitions() {
		return List.of(createOx0003To0x0080Definition());
	}

	private ModbusRegistryDefnition createOx0003To0x0080Definition() {
		return new ModbusRegisterDefinitionBuilder(0x003, 0x080, this::updateState)
				.setFirstBytesEater(28)
				.add(0x0003, "inverter-id", "Inverter Id", ChannelTypes.SERIAL_NO,
						ExtractingBuilder.extractBytes(10).asString())
				.add(0x0010, "rated-power", "Rated power", ChannelTypes.ELECTRIC_POWER,
						ExtractingBuilder.extactShort().divided(10).as(Units.WATT))
				.add(0x003C, "daily-production", "Daily production", ChannelTypes.ELECTRICAL_ENERGY,
						ExtractingBuilder.extactShort().multiplied(100).as(Units.WATT_HOUR))
				.add(0x003f, "total-production", "Total production", ChannelTypes.ELECTRICAL_ENERGY,
						ExtractingBuilder.extractInteger().revertedShorts().multiplied(100)
								.as(Units.WATT_HOUR))
				.add(0x0045, "total-production-1", "Total production 1", ChannelTypes.ELECTRICAL_ENERGY,
						Extractors.SHORT_AS_THENTS_OF_WATT_HOUR)
				.add(0x0047, "total-production-2", "Total production 2", ChannelTypes.ELECTRICAL_ENERGY,
						Extractors.SHORT_AS_THENTS_OF_WATT_HOUR)
				.add(0x0049, "ac-voltage", "AC voltage", ChannelTypes.ELECTRIC_VOLTAGE,
						Extractors.SHORT_AS_TEHNTS_OF_VOLT)
				.add(0x004F, "ac-frequency", "AC frequency", ChannelTypes.AC_FREQUENCY,
						Extractors.HUNDRETHS_OF_HERZ)
				.add(0x004C, "grid-current", "Grid current", ChannelTypes.ELECTRIC_CURRENT,
						Extractors.SHORT_AS_TENTHS_OF_AMPERE)
				.add(0x006D, "pv1-voltage", "PV1 Voltage", ChannelTypes.ELECTRIC_VOLTAGE,
						Extractors.SHORT_AS_TEHNTS_OF_VOLT)
				.add(0x006F, "pv2-voltage", "PV2 Voltate", ChannelTypes.ELECTRIC_VOLTAGE,
						Extractors.SHORT_AS_TEHNTS_OF_VOLT)
				.add(0x006E, "pv1-current", "PV1 current", ChannelTypes.ELECTRIC_CURRENT,
						Extractors.SHORT_AS_TENTHS_OF_AMPERE)
				.add(0x0070, "pv2-current", "PV2 current", ChannelTypes.ELECTRIC_CURRENT,
						Extractors.SHORT_AS_TENTHS_OF_AMPERE)
				.add(0x003B, "operating-state", "Operating state", ChannelTypes.DEYE_01_OPERATING_STATE,
						ExtractingBuilder.extactShort().mappedTo(OPERATING_STATES))
				.add(0x0056, "total-output-power", "Total output power", ChannelTypes.ELECTRIC_POWER,
						ExtractingBuilder.extractInteger().revertedShorts().divided(10)
								.as(Units.WATT))
				.add(0x005a, "radiator-temperature", "Radiation temperature",
						ChannelTypes.INTERNAL_TEMPERATURE,
						ExtractingBuilder.extactShort().divided(100).as(SIUnits.CELSIUS))
				.build();
	}

}
