package org.openhab.binding.lswlogger.internal.sn23x;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.openhab.binding.lswlogger.internal.ByteUtils;
import org.openhab.binding.lswlogger.internal.protocolv5.sn23x.From0404To042eDataResponseHandler;
import org.openhab.binding.lswlogger.internal.protocolv5.sn23x.From0480To04afDataResponseHandler;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.unit.Units;
import org.openhab.core.types.State;

public class SN23xDataResponseHandlersTest {

    @Test
    void givenHexResolvesToProperUpdates1() {
        // givem
        ByteBuffer buffer = ByteUtils.fromHex(
                "A5:67:00:10:15:00:4A:A3:DC:AC:8B:02:01:9D:DF:29:00:46:03:00:00:DF:32:D4:63:01:03:54:00:02:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:3C:00:23:00:00:00:1A:00:00:00:00:00:00:00:00:00:00:00:19:00:00:00:00:00:00:00:00:00:00:02:1C:00:00:9A:B4:00:00:B0:1C:03:27:00:17:00:02:E5:8B:9C:15:");
        buffer.flip();
        Map<String, State> updates = new HashMap<>();
        From0404To042eDataResponseHandler handler = new From0404To042eDataResponseHandler(updates::put);
        // when
        handler.handle(buffer);
        // then
        assertThat(updates).hasSize(20)
                .containsEntry("countdown", new QuantityType<>(0, Units.SECOND));
    }

    @Test
    void givenHexResolvesToProperUpdates2() {
        // givem
        ByteBuffer buffer = ByteUtils.fromHex(
                "A5:8D:00:10:15:00:4B:A3:DC:AC:8B:02:01:B8:DF:29:00:61:03:00:00:DF:32:D4:63:01:03:7A:00:00:82:18:43:08:61:7F:13:86:00:1F:FF:D5:00:39:00:00:00:00:00:00:00:00:00:00:09:23:00:50:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:08:F9:00:55:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:09:21:00:4F:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:4F:44:08:15:");
        buffer.flip();
        Map<String, State> updates = new HashMap<>();
        From0480To04afDataResponseHandler handler = new From0480To04afDataResponseHandler(updates::put);
        // when
        handler.handle(buffer);
        // then
        assertThat(updates).hasSize(49);
    }

}
