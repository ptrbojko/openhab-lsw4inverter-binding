package org.openhab.binding.lswlogger.internal.connection;

public interface StateMachineSwitchable {
    void switchToNextState();

    void switchToAlternativeState();

    void switchToExceptionState();

    void switchToErrorState();

}