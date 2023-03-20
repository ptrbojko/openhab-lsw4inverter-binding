package org.openhab.binding.lswlogger.internal.protocolv5.states;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
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
    private final BlockingQueue<ProtocolStateMeta<T, C>> next;
    private ProtocolStateMeta<T, C> current;
    private Optional<ScheduledFuture<?>> future = Optional.empty();

    public StateMachine(@NonNull Map<ProtocolState<T, C>, ProtocolStateMeta<T, C>> states,
            @NonNull ProtocolState<T, C> initialState, @NonNull C context,
            @NonNull ScheduledExecutorService scheduler) {
        this.states = states;
        this.next = new ArrayBlockingQueue<>(3);
        this.next.add(states.get(initialState));
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

    public void stop() {
        logger.warn("Trying to stop state machine");
        try {
            next.put(createTerminalNextState());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        future.ifPresent(sf -> sf.cancel(true));
    }

    private ProtocolStateMeta<T, C> createTerminalNextState() {
        return new ProtocolStateMetaImpl<T, C>(new FinalState<T, C>(), null, null, null, null);
    }

    public void run() {
        try {
            while (true) {
                try {
                    logger.debug("Waiting for state to process");
                    current = next.take();
                } catch (InterruptedException e) {
                    logger.warn("Interruped - state machine ends", e);
                }
                logger.debug("Entering {}", current.getState());
                current.getState().handle(this, context);
                logger.debug("Leaving {}", current.getState());
                if (current.getState() instanceof FinalState) {
                    logger.info("Entering final state, state machine ends");
                    return;
                }
            }
        } catch (Throwable t) {
            logger.error("AAAA", t);
        }
    }

    @Override
    public void schedule(int time, TimeUnit unit, Runnable job) {
        logger.debug("Trying to schedule task in {} {} during state {}", time, unit, current.getState());

        if (!future.map(ScheduledFuture::isDone).orElse(true)) {
            logger.error("Trying to schedule more than one task!", new IllegalStateException()); // ;)
        }
        future = Optional.of(scheduler.schedule(() -> {
            switchTo(new ProtocolStateMetaRunnableWrapper<T, C>(current, job));
        }, time, unit));
    }

    private void switchTo(ProtocolState<T, C> state) {
        if (!next.isEmpty()) {
            logger.error("Current state has already set next state. Current {}, next {}, requested {} ",
                    current.getState(), next.peek().getState(), state);
            return;
        }
        next.add(states.get(state));
    }

    private void switchTo(ProtocolStateMeta<T, C> stateMeta) {
        if (!next.isEmpty()) {
            logger.error("Current state has already set next state. Current {}, next {}, requested {} ",
                    current.getState(), next.peek().getState(), stateMeta.getState());
            return;
        }
        next.add(stateMeta);
    }

    private static class ProtocolStateMetaRunnableWrapper<T, C extends Context<T>> implements ProtocolStateMeta<T, C> {

        private final ProtocolStateMeta<T, C> wrapped;
        private final ProtocolState<T, C> runnableWrapper;

        public ProtocolStateMetaRunnableWrapper(ProtocolStateMeta<T, C> wrapped, Runnable runnable) {
            this.wrapped = wrapped;
            this.runnableWrapper = (@NonNull StateMachineSwitchable stateMachine, @NonNull C context) -> {
                runnable.run();
            };
        }

        @Override
        public ProtocolState<T, C> getState() {
            return runnableWrapper;
        }

        @Override
        public ProtocolState<T, C> getNextState() {
            return wrapped.getNextState();
        }

        @Override
        public ProtocolState<T, C> getAlternativeState() {
            return wrapped.getAlternativeState();
        }

        @Override
        public ProtocolState<T, C> getExceptioState() {
            return wrapped.getExceptioState();
        }

        @Override
        public ProtocolState<T, C> getErrorState() {
            return wrapped.getErrorState();
        }

        @Override
        public String toString() {
            return "ProtocolStateMetaRunnableWrapper [wrapped=" + wrapped + "]";
        }

    }
}
