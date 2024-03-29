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

import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.DebugLoggerV5;
import org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.Deye01;
import org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.LSWLoggerV5;
import org.openhab.binding.lswlogger.internal.LswLoggerBindingConstants.SN23xV5;
import org.openhab.binding.lswlogger.internal.protocolv5.thing.debug.DebugLoggerHandler;
import org.openhab.binding.lswlogger.internal.protocolv5.thing.deye.DeyeLoggerHandler;
import org.openhab.binding.lswlogger.internal.protocolv5.thing.g3hyd.SN23xLoggerHandler;
import org.openhab.binding.lswlogger.internal.protocolv5.thing.lsw3.LswLoggerHandler;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Component;

/**
 * The {@link LswLoggerHandlerFactory} is responsible for creating things and
 * thing
 * handlers.
 *
 * @author Piotr Bojko - Initial contribution
 */
@Component(configurationPid = "binding.lswlogger", service = ThingHandlerFactory.class)
public class LswLoggerHandlerFactory extends BaseThingHandlerFactory {

    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Set.of(
            DebugLoggerV5.THING_TYPE_ID,
            LSWLoggerV5.THING_TYPE_ID,
            SN23xV5.THING_TYPE_ID,
            Deye01.THING_TYPE_ID);

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (LSWLoggerV5.THING_TYPE_ID.equals(thingTypeUID)) {
            return new LswLoggerHandler(thing);
        }

        if (SN23xV5.THING_TYPE_ID.equals(thingTypeUID)) {
            return new SN23xLoggerHandler(thing);
        }

        if (DebugLoggerV5.THING_TYPE_ID.equals(thingTypeUID)) {
            return new DebugLoggerHandler(thing);
        }

        if (Deye01.THING_TYPE_ID.equals(thingTypeUID)) {
            return new DeyeLoggerHandler(thing);
        }

        return null;
    }
}
