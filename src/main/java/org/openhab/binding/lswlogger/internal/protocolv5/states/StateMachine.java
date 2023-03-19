package org.openhab.binding.lswlogger.internal.protocolv5.states;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.lswlogger.internal.connection.Context;
import org.openhab.binding.lswlogger.internal.connection.StateMachineSwitchable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StateMachine<T, C extends Context<T>> implements StateMachineSwitchable {

    private static final Logger logger = LoggerFactory.getLogger(StateMachine.class);

    private final Map<ProtocolState<T, C>, ProtocolStateMeta<T, C>> states;
    @NonNull
    private final ScheduledExecutorService scheduler;
    @NonNull
    private final C context;
    private ProtocolStateMeta<T, C> next;
    private ProtocolStateMeta<T, C> current;
    private Optional<ScheduledFuture<?>> future = Optional.empty();

    public StateMachine(@NonNull Map<ProtocolState<T, C>, ProtocolStateMeta<T, C>> states,
            @NonNull ProtocolState<T, C> initialState, @NonNull C context,
            @NonNull ScheduledExecutorService scheduler) {
        this.states = states;
        this.next = states.get(initialState);
        this.context = context;
        this.scheduler = scheduler;
    }

    @Override
    public void switchToNextState() {
        switchTo(current.getNextState());
    }

    @Override
    public void switchToAlternativeState() {
        switchTo(current.getAlternativeState());
    }

    @Override
    public void switchToExceptionState() {
        switchTo(current.getExceptioState());
    }

    @Override
    public void switchToErrorState() {
        switchTo(current.getErrorState());
    }

    public synchronized void stop() {
        logger.warn("Trying to stop state machine");
        next = createTerminalNextState();
        future.ifPresent(sf -> sf.cancel(true));
    }

    private ProtocolStateMeta<T, C> createTerminalNextState() {
        return new ProtocolStateMeta<T, C>(new FinalState<T, C>(), null, null, null, null);
    }

    public void run() {
        while (next != null) {
            current = next;
            next = null;
            logger.debug("Entering {}", current.getState());
            current.getState().handle(this, context);
            logger.debug("Leaving {}", current.getState());
        }
        logger.debug("No immediate next state from current {}", current.getState());
    }

    @Override
    public synchronized void schedule(int time, TimeUnit unit, Runnable job) {
        logger.debug("Trying to schedulle task in {} {} during state {}", time, unit, current.getState());

        if (future.map(ScheduledFuture::isDone).orElse(true)) {
            logger.error("Trying to schedule more than one task!", new IllegalStateException()); // ;)
        }
        future = Optional.of(scheduler.schedule(job, time, unit));
    }

    private synchronized void switchTo(ProtocolState<T, C> state) {
        if (next != null) {
            logger.error("Current state has already set next state. Current {}, next {}, requested {} ",
                    current.getState(), next.getState(), state);
            return;
        }
        next = states.get(state);
    }

}
