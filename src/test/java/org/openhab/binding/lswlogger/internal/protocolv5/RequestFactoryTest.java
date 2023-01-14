package org.openhab.binding.lswlogger.internal.protocolv5;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;
import org.openhab.binding.lswlogger.internal.ByteUtils;

public class RequestFactoryTest {

    private final int SN = 1717236526;

    @Test
    void testCreate() {
        // given
        // when
        ByteBuffer message = RequestFactory.create(SN);
        // then
        String hex = ByteUtils.toHex(message.rewind());
        assertThat(hex).isEqualTo(
                "A5:17:00:10:45:00:00:2E:F3:5A:66:02:00:00:00:00:00:00:00:00:00:00:00:00:00:00:01:03:00:00:00:27:05:D0:4F:15:");
    }
}
