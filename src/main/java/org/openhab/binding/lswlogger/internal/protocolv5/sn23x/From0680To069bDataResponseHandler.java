package org.openhab.binding.lswlogger.internal.protocolv5.sn23x;

import java.nio.ByteBuffer;

import org.openhab.binding.lswlogger.internal.ExtractingUtils;
import org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.SN23xV5.From0680To069bChannels;
import org.openhab.binding.lswlogger.internal.protocolv5.AbstractDataResponseHandler;
import org.openhab.binding.lswlogger.internal.protocolv5.ExtractorsBuilder;

public class From0680To069bDataResponseHandler extends AbstractDataResponseHandler {

    public From0680To069bDataResponseHandler(ExtractorsBuilder.ChannelStateUpdate stateUpdate) {
        super(new ExtractorsBuilder(stateUpdate).addBytesEater(28)
                .addBytesEater(8)
                .add(From0680To069bChannels.todayEnergyProduction, ExtractingUtils::shortToHundrethsAsKiloWattHour)
                .add(From0680To069bChannels.totalEnergyProduction, ExtractingUtils::intToDecimalAsKiloWattHour)
                .add(From0680To069bChannels.todayEnergyConsumption, ExtractingUtils::intToDecimalAsKiloWattHour)
                .add(From0680To069bChannels.totalEnergyConsumption, ExtractingUtils::intToDecimalAsKiloWattHour)
                .add(From0680To069bChannels.todayEnergyPurchased, ExtractingUtils::intToDecimalAsKiloWattHour)
                .add(From0680To069bChannels.totalEnergyPurchased, ExtractingUtils::intToDecimalAsKiloWattHour)
                .add(From0680To069bChannels.todayEnergySold, ExtractingUtils::intToDecimalAsKiloWattHour)
                .add(From0680To069bChannels.totalEnergySold, ExtractingUtils::intToDecimalAsKiloWattHour)
                .add(From0680To069bChannels.todayBatteryCharged, ExtractingUtils::intToDecimalAsKiloWattHour)
                .add(From0680To069bChannels.totalBatteryCharged, ExtractingUtils::intToDecimalAsKiloWattHour)
                .add(From0680To069bChannels.todayBatteryDischarged, ExtractingUtils::intToDecimalAsKiloWattHour)
                .add(From0680To069bChannels.totalBatteryDischarged, ExtractingUtils::intToDecimalAsKiloWattHour));
    }

    @Override
    protected boolean accepts(ByteBuffer buffer) {
        return buffer.remaining() > 16;
    }

}
