package org.openhab.binding.lswlogger.internal.protocolv5.modbus;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.builder.ChannelBuilder;
import org.openhab.core.thing.binding.builder.ThingBuilder;
import org.openhab.core.thing.type.ChannelKind;
import org.openhab.core.thing.type.ChannelTypeUID;

public class ChannelConfigurer {

    private final Collection<ModbusRegistryValueDefinition> definitions;

    ChannelConfigurer(Collection<ModbusRegistryValueDefinition> definitions) {
        this.definitions = definitions;
    }

    public void configure(Thing thing, ThingBuilder builder) {
        List<Channel> channels = Stream
                .concat(thing.getChannels().stream(),
                        definitions.stream().map(definition -> toChannel(thing.getUID(), definition)))
                .collect(Collectors.toList());
        builder.withChannels(channels);
    }

    private Channel toChannel(ThingUID thingUUID, ModbusRegistryValueDefinition definition) {
        return ChannelBuilder.create(new ChannelUID(thingUUID, definition.getChannelId()))
                .withLabel(definition.getChannelName())
                .withKind(ChannelKind.STATE)
                .withType(new ChannelTypeUID(definition.getChannelType()))
                .build();
    }
}
