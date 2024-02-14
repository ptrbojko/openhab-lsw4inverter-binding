package org.openhab.binding.lswlogger.internal.protocolv5.states;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.openhab.binding.lswlogger.internal.connection.Context;
import org.openhab.binding.lswlogger.internal.connection.StateMachineSwitchable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StateMachine<T, C extends Context<T>> implements StateMachineSwitchable {

    private static final Logger logger = LoggerFactory.getLogger(StateMachine.class);

    private final Map<ProtocolState<T, C>, ProtocolStateMeta<T, C>> states;
    private final ScheduledExecutorService scheduler;
    private final C context;
    private final BlockingQueue<ProtocolStateMeta<T, C>> nextStateQueue;
    private ProtocolStateMeta<T, C> current;
    private Optional<ScheduledFuture<?>> future = Optional.empty();

    public StateMachine(Map<ProtocolState<T, C>, ProtocolStateMeta<T, C>> states,
            ProtocolState<T, C> initialState, C context,
            ScheduledExecutorService scheduler) {
        this.states = states;
        this.nextStateQueue = new ArrayBlockingQueue<>(3);
        this.nextStateQueue.add(states.get(initialState));
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
            nextStateQueue.put(createTerminalNextState());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        future.ifPresent(sf -> sf.cancel(true));
    }

    private ProtocolStateMeta<T, C> createTerminalNextState() {
        return new ProtocolStateMetaImpl<T, C>("Final", new FinalState<T, C>(), null, null, null, null);
    }

    public void run() {
        while (true) {
            try {
                logger.debug("Waiting for state to process");
                current = nextStateQueue.take();
                logger.debug("Taken state {} to process", current.getDescription());
                future.ifPresent(this::waitForScheduledTask);
            } catch (InterruptedException e) {
                logger.error("Interruped - processing ends", e);
                return;
            }
            logger.debug("Entering {}", current.getDescription());
            current.handle(this, context);
            logger.debug("Leaving {}", current.getDescription());
            if (current.getState() instanceof FinalState) {
                logger.info("Final state met, processing ends");
                return;
            }
        }
    }

    private void waitForScheduledTask(ScheduledFuture<?> sf) {
        try {
            sf.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.warn("Scheduled task interrupted", e);
        }
    }

    @Override
    public void schedule(int time, TimeUnit unit, Runnable job) {
        logger.debug("Trying to schedule task in {} {} during state {}", time, unit, current.getDescription());

        if (!future.map(ScheduledFuture::isDone).orElse(true)) {
            logger.error("Trying to schedule more than one task!", new IllegalStateException()); // ;)
            return;
        }
        future = Optional
                .of(scheduler.schedule(() -> switchTo(new MetaRunnableWrapper<T, C>(current, job)), time, unit));
    }

    private void switchTo(ProtocolState<T, C> state) {
        switchTo(states.get(state));
    }

    private void switchTo(ProtocolStateMeta<T, C> stateMeta) {
        if (!nextStateQueue.isEmpty()) {
            logger.error("Current state has already set next state. Current {}, next {}, requested {} ",
                    current.getState(), nextStateQueue.peek().getState(), stateMeta.getState());
            return;
        }
        nextStateQueue.add(stateMeta);
    }

    private static class MetaRunnableWrapper<T, C extends Context<T>> extends ProtocolStateMetaImpl<T, C> {

        private final Runnable runnable;

        public MetaRunnableWrapper(ProtocolStateMeta<T, C> wrapped, Runnable runnable) {
            super(wrapped.getDescription(), wrapped.getState(), wrapped.getNextState(), wrapped.getAlternativeState(),
                    wrapped.getExceptioState(), wrapped.getErrorState());
            this.runnable = runnable;

        }

        @Override
        public void handle(StateMachineSwitchable stateMachine, C context) {
            runnable.run();
        }

        @Override
        public String getDescription() {
            return "Wrapper [" + super.getDescription() + "]";
        }
    }
}
