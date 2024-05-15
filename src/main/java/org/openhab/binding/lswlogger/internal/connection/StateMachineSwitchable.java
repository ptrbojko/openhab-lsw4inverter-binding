package org.openhab.binding.lswlogger.internal.connection;

import java.util.concurrent.TimeUnit;

public interface StateMachineSwitchable {
    void switchToNextState();

    void switchToAlternativeState();

    void switchToExceptionState();

    void switchToErrorState();

    void schedule(long calculateWaitPeriod, TimeUnit unit, Runnable runnable);

}