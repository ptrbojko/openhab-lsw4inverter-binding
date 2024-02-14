package org.openhab.binding.lswlogger.internal.protocolv5.modbus;

import java.util.Collection;
import java.util.Map;

import org.openhab.binding.lswlogger.internal.ChannelTypes;
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
        definitions.stream()
                .map(definition -> toChannel(thing.getUID(), definition))
                .forEach(builder::withChannel);
    }

    private Channel toChannel(ThingUID thingUUID, ModbusRegistryValueDefinition definition) {
        ChannelTypes channelType = definition.getChannelType();
        return ChannelBuilder.create(new ChannelUID(thingUUID, definition.getChannelId()))
                .withLabel(definition.getChannelName())
                .withKind(ChannelKind.STATE)
                .withType(new ChannelTypeUID(channelType.getTypeId()))
                .withAcceptedItemType(channelType.getAcceptedItemType())
                .withProperties(Map.of(DynamicChannels.DYNAMIC, "yes"))
                .build();
    }
}
