package org.openhab.binding.lswlogger.internal.protocolv5.thing.sn23x;

import static org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.SN23xV5.OPERATING_STATES;

import java.nio.ByteBuffer;

import org.openhab.binding.lswlogger.internal.ExtractingUtils;
import org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.SN23xV5.From0404To042eChannels;
import org.openhab.binding.lswlogger.internal.protocolv5.AbstractDataResponseHandler;
import org.openhab.binding.lswlogger.internal.protocolv5.ExtractorsBuilder;

public class From0404To042eDataResponseHandler extends AbstractDataResponseHandler {

    private static final int MESSAGE_LENGTH = 116;

    public From0404To042eDataResponseHandler(ExtractorsBuilder.ChannelStateUpdate stateUpdate) {
        super(new ExtractorsBuilder(stateUpdate).addBytesEater(28)
                .add(From0404To042eChannels.operatingStateChannel,
                        v -> ExtractingUtils.extractShortToToStringType(v, OPERATING_STATES))
                .add(From0404To042eChannels.faultChannel, ExtractingUtils.bytesToString(24))
                .add(From0404To042eChannels.countdown, ExtractingUtils::shortToSeconds)
                .add(From0404To042eChannels.env1Temperature, ExtractingUtils::shortToTemperature)
                .add(From0404To042eChannels.env2Temperature, ExtractingUtils::shortToTemperature)
                .add(From0404To042eChannels.heatSink1Temperature, ExtractingUtils::shortToTemperature)
                .add(From0404To042eChannels.heatSink2Temperature, ExtractingUtils::shortToTemperature)
                .add(From0404To042eChannels.heatSink3Temperature, ExtractingUtils::shortToTemperature)
                .add(From0404To042eChannels.heatSink4Temperature, ExtractingUtils::shortToTemperature)
                .add(From0404To042eChannels.heatSink5Temperature, ExtractingUtils::shortToTemperature)
                .add(From0404To042eChannels.heatSink6Temperature, ExtractingUtils::shortToTemperature)
                .add(From0404To042eChannels.inv1Temperature, ExtractingUtils::shortToTemperature)
                .add(From0404To042eChannels.inv2Temperature, ExtractingUtils::shortToTemperature)
                .add(From0404To042eChannels.inv3Temperature, ExtractingUtils::shortToTemperature)
                .add(From0404To042eChannels.rsvd1Temperature, ExtractingUtils::shortToTemperature)
                .add(From0404To042eChannels.rsvd2Temperature, ExtractingUtils::shortToTemperature)
                .add(From0404To042eChannels.rsvd3Temperature, ExtractingUtils::shortToTemperature)
                .add(From0404To042eChannels.todayGenerationTime, ExtractingUtils::shortAsMinute)
                .add(From0404To042eChannels.totalGenerationTime, ExtractingUtils::intToHours)
                .addBytesEater(2)
                .add(From0404To042eChannels.totalServiceTime, ExtractingUtils::intToHours)

        );
    }

    @Override
    protected boolean accepts(ByteBuffer buffer) {
        return buffer.remaining() == MESSAGE_LENGTH;
    }

}
