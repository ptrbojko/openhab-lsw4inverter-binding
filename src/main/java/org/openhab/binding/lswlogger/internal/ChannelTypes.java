package org.openhab.binding.lswlogger.internal;

public enum ChannelTypes {
    SERIAL_NO("serial-no", true),
    ELECTRIC_POWER("electric-power", false),
    ELECTRIC_VOLTAGE("electric-voltage", false),
    ELECTRIC_CURRENT("electric-current", false),
    ELECTRICAL_ENERGY("electrical-energy", false),
    AC_FREQUENCY("ac-frequency", true),
    DEYE_01_OPERATING_STATE("deye-01-operating-state", true),
    OPERATING_STATE("operating-state", true),
    INTERNAL_TEMPERATURE("internal-temperature", true),
    TIME_PERIOD("time-period", true),
    COUNTRY_CODE("country-code", true),
    FACTOR("powerfactor-channel", true);

    private final String id;

    ChannelTypes(String id, boolean custom) {
        this.id = custom ? LswLoggerBindingConstants.BINDING_ID + ":" + id : "system:" + id;
    }

    public String toId() {
        return id;
    }
}
