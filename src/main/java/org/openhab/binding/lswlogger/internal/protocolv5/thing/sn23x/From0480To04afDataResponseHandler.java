package org.openhab.binding.lswlogger.internal.protocolv5.thing.sn23x;

import java.nio.ByteBuffer;

import org.openhab.binding.lswlogger.internal.ExtractingUtils;
import org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.SN23xV5.From0480To04afChannels;
import org.openhab.binding.lswlogger.internal.protocolv5.AbstractDataResponseHandler;
import org.openhab.binding.lswlogger.internal.protocolv5.ExtractorsBuilder;

public class From0480To04afDataResponseHandler extends AbstractDataResponseHandler {

    private static final int MESSAGE_LENGTH = 154;

    public From0480To04afDataResponseHandler(ExtractorsBuilder.ChannelStateUpdate stateUpdate) {
        super(new ExtractorsBuilder(stateUpdate).addBytesEater(28)
                .addBytesEater(8)
                .add(From0480To04afChannels.gridFrequency, ExtractingUtils::shortToHundrethsAsHz)
                .add(From0480To04afChannels.totalOutputActivePower, ExtractingUtils::shortMultiple10AsWatts)
                .add(From0480To04afChannels.totalOutputReactivePower, ExtractingUtils::shortMultiple10AsWatts)
                .add(From0480To04afChannels.totalOutputApparentPower, ExtractingUtils::shortMultiple10AsWatts)
                .add(From0480To04afChannels.totalPCCActivePower, ExtractingUtils::shortMultiple10AsWatts)
                .add(From0480To04afChannels.totalPCCReactivePower, ExtractingUtils::shortMultiple10AsWatts)
                .add(From0480To04afChannels.totalPCCApparentPower, ExtractingUtils::shortMultiple10AsWatts)
                .addBytesEater(4)
                // .add("GridOutput_Rsvd1", ExtractingUtils.bytesToString(2))
                // .add("GridOutput_Rsvd2", ExtractingUtils.bytesToString(2))
                .add(From0480To04afChannels.phaseRVoltage, ExtractingUtils::shortToTenthsAsVoltage)
                .add(From0480To04afChannels.phaseRCurrent, ExtractingUtils::shortToHundrethsAsAmpers)
                .add(From0480To04afChannels.phaseRActivePower, ExtractingUtils::shortMultiple10AsWatts)
                .add(From0480To04afChannels.phaseRReactivePower, ExtractingUtils::shortMultiple10AsWatts)
                .add(From0480To04afChannels.phaseRPowerFactor, ExtractingUtils::shortToThousandth)
                .add(From0480To04afChannels.phaseRPCCCurrent, ExtractingUtils::shortToHundrethsAsAmpers)
                .add(From0480To04afChannels.phaseRPCCActivePower, ExtractingUtils::shortMultiple10AsWatts)
                .add(From0480To04afChannels.phaseRPCCReactivePower, ExtractingUtils::shortMultiple10AsWatts)
                .add(From0480To04afChannels.phaseRPCCPowerFactor, ExtractingUtils::shortToThousandth)
                .addBytesEater(4)
                // .add("R_Rsvd1", ExtractingUtils.bytesToString(2))
                // .add("R_Rsvd2", ExtractingUtils.bytesToString(2))
                .add(From0480To04afChannels.phaseSVoltage, ExtractingUtils::shortToTenthsAsVoltage)
                .add(From0480To04afChannels.phaseSCurrent, ExtractingUtils::shortToHundrethsAsAmpers)
                .add(From0480To04afChannels.phaseSActivePower, ExtractingUtils::shortMultiple10AsWatts)
                .add(From0480To04afChannels.phaseSReactivePower, ExtractingUtils::shortMultiple10AsWatts)
                .add(From0480To04afChannels.phaseSPowerFactor, ExtractingUtils::shortToThousandth)
                .add(From0480To04afChannels.phaseSPCCCurrent, ExtractingUtils::shortToHundrethsAsAmpers)
                .add(From0480To04afChannels.phaseSPCCActivePower, ExtractingUtils::shortMultiple10AsWatts)
                .add(From0480To04afChannels.phaseSPCCReactivePower, ExtractingUtils::shortMultiple10AsWatts)
                .add(From0480To04afChannels.phaseSPCCPowerFactor, ExtractingUtils::shortToThousandth)
                .addBytesEater(4)
                // .add("S_Rsvd1", ExtractingUtils.bytesToString(2))
                // .add("S_Rsvd2", ExtractingUtils.bytesToString(2))
                .add(From0480To04afChannels.phaseTVoltage, ExtractingUtils::shortToTenthsAsVoltage)
                .add(From0480To04afChannels.phaseTCurrent, ExtractingUtils::shortToHundrethsAsAmpers)
                .add(From0480To04afChannels.phaseTActivePower, ExtractingUtils::shortMultiple10AsWatts)
                .add(From0480To04afChannels.phaseTReactivePower, ExtractingUtils::shortMultiple10AsWatts)
                .add(From0480To04afChannels.phaseTPowerFactor, ExtractingUtils::shortToThousandth)
                .add(From0480To04afChannels.phaseTPCCCurrent, ExtractingUtils::shortToHundrethsAsAmpers)
                .add(From0480To04afChannels.phaseTPCCActivePower, ExtractingUtils::shortMultiple10AsWatts)
                .add(From0480To04afChannels.phaseTPCCReactivePower, ExtractingUtils::shortMultiple10AsWatts)
                .add(From0480To04afChannels.phaseTPCCPowerFactor, ExtractingUtils::shortToThousandth)
                .addBytesEater(4)
                // .add("T_Rsvd1", ExtractingUtils.bytesToString(2))
                // .add("T_Rsvd2", ExtractingUtils.bytesToString(2))
                .add(From0480To04afChannels.pvExtActivePower, ExtractingUtils::shortMultiple10AsWatts)
                .add(From0480To04afChannels.loadSysActivePower, ExtractingUtils::shortMultiple10AsWatts)
                .add(From0480To04afChannels.phaseL1NVoltage, ExtractingUtils::shortToTenthsAsVoltage)
                .add(From0480To04afChannels.phaseL1NCurrent, ExtractingUtils::shortToHundrethsAsAmpers)
                .add(From0480To04afChannels.phaseL1NActivePower, ExtractingUtils::shortMultiple10AsWatts)
                .add(From0480To04afChannels.phaseL1NPCCCurrent, ExtractingUtils::shortToHundrethsAsAmpers)
                .add(From0480To04afChannels.phaseL1NPCCActivePower, ExtractingUtils::shortMultiple10AsWatts)
                .add(From0480To04afChannels.phaseL2NVoltage, ExtractingUtils::shortToTenthsAsVoltage)
                .add(From0480To04afChannels.phaseL2NCurrent, ExtractingUtils::shortToHundrethsAsAmpers)
                .add(From0480To04afChannels.phaseL2NActivePower, ExtractingUtils::shortMultiple10AsWatts)
                .add(From0480To04afChannels.phaseL2NPCCCurrent, ExtractingUtils::shortToHundrethsAsAmpers)
                .add(From0480To04afChannels.phaseL2NPCCActivePower, ExtractingUtils::shortMultiple10AsWatts)
                .add(From0480To04afChannels.lineL1Voltage, ExtractingUtils::shortToTenthsAsVoltage)
                .add(From0480To04afChannels.lineL2Voltage, ExtractingUtils::shortToTenthsAsVoltage)
                .add(From0480To04afChannels.lineL3Voltage, ExtractingUtils::shortToTenthsAsVoltage));
    }

    @Override
    protected boolean accepts(ByteBuffer buffer) {
        return MESSAGE_LENGTH == buffer.remaining();
    }

}
