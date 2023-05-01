package org.openhab.binding.lswlogger.internal.protocolv5.requests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openhab.binding.lswlogger.internal.ByteUtils;
import org.openhab.binding.lswlogger.internal.connection.Channel;
import org.openhab.binding.lswlogger.internal.connection.Context;
import org.openhab.binding.lswlogger.internal.connection.StateMachineSwitchable;
import org.openhab.binding.lswlogger.internal.protocolv5.thing.debug.ConfigurableSendingRequestState;
import org.openhab.binding.lswlogger.internal.protocolv5.thing.debug.DebugLoggerThingConfiguration;

@ExtendWith(MockitoExtension.class)
public class ConfigurableSendingRequestStateTest {

    private final long SN = 1717236526;

    @Mock
    private Context<DebugLoggerThingConfiguration> context;

    @Mock
    private DebugLoggerThingConfiguration config;

    @Mock
    private Channel channel;

    @Mock
    private StateMachineSwitchable sm;

    @Captor
    private ArgumentCaptor<ByteBuffer> bufferCaptor;

    private ConfigurableSendingRequestState<Context<DebugLoggerThingConfiguration>> state = new ConfigurableSendingRequestState<>();

    @Test
    void shouldConstructValidRequest() {
        // given
        given(context.config()).willReturn(config);
        given(context.channel()).willReturn(channel);
        given(config.getSerialNumber()).willReturn(SN);
        given(config.getStartRegister()).willReturn("0x0000");
        given(config.getEndRegister()).willReturn("0x0027");
        // when
        state.handle(sm, context);
        // then
        verify(channel, times(1)).write(bufferCaptor.capture(), any(), any());
        String hex = ByteUtils.toHex(bufferCaptor.getValue().rewind());
        assertThat(hex).isEqualTo(
                "A5:17:00:10:45:00:00:2E:F3:5A:66:02:00:00:00:00:00:00:00:00:00:00:00:00:00:00:01:03:00:00:00:27:05:D0:4F:15:");
    }

}
