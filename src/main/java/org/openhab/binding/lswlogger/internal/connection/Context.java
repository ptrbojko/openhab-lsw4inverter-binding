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
package org.openhab.binding.lswlogger.internal.connection;

import org.openhab.core.thing.ThingStatus;
import org.openhab.core.types.State;

public interface Context<T> {

    Channel channel();

    void notifyLoggerIsOffline();

    void notifyCannotRecover();

    void updateState(String uuid, State state);

    void updateStatus(ThingStatus online);

    T config();

}
