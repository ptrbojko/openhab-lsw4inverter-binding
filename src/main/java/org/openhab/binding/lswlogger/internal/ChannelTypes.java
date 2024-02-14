package org.openhab.binding.lswlogger.internal;

import org.openhab.core.library.CoreItemFactory;

public enum ChannelTypes {
    SERIAL_NO("serial-no", true, CoreItemFactory.STRING),
    ELECTRIC_POWER("electric-power", false, CoreItemFactory.NUMBER+":Power"),
    ELECTRIC_VOLTAGE("electric-voltage", false, CoreItemFactory.NUMBER+":ElectricPotential"),
    ELECTRIC_CURRENT("electric-current", false, CoreItemFactory.NUMBER+":ElectricCurrent"),
    ELECTRICAL_ENERGY("electric-energy", false, CoreItemFactory.NUMBER+":Energy"),
    AC_FREQUENCY("ac-frequency", true, CoreItemFactory.NUMBER+":Frequency"),
    DEYE_01_OPERATING_STATE("deye-01-operating-state", true, CoreItemFactory.STRING),
    OPERATING_STATE("operating-state", true, CoreItemFactory.STRING),
    INTERNAL_TEMPERATURE("internal-temperature", true, CoreItemFactory.NUMBER+":Temperature"),
    TIME_PERIOD("time-period", true, CoreItemFactory.NUMBER),
    COUNTRY_CODE("country-code", true, CoreItemFactory.STRING),
    FACTOR("powerfactor-channel", true, CoreItemFactory.NUMBER);

    private final String id;
    private final String acceptedItemType;

    ChannelTypes(String id, boolean custom, String acceptedItemType) {
        this.id = custom ? LswLoggerBindingConstants.BINDING_ID + ":" + id : "system:" + id;
        this.acceptedItemType = acceptedItemType;
    }

    public String getTypeId() {
        return id;
    }

    public String getAcceptedItemType() {
        return acceptedItemType;
    }
}
