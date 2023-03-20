package org.openhab.binding.lswlogger.internal.protocolv5.states;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.lswlogger.internal.LoggerThingConfiguration;
import org.openhab.binding.lswlogger.internal.connection.Context;

public class StateMachineBuilder<T, C extends Context<T>> {

    private final Map<ProtocolState<T, C>, ProtocolStateMeta<T, C>> states = new HashMap<>();
    private C context;
    private ProtocolState<T, C> initialState;
    private ScheduledExecutorService scheduler;

    public StateMachineBuilder<T, C> addState(String description, ProtocolState<T, C> state, Consumer<RouteConfigurer<T, C>> routeConfiguration) {
        RouteBuilder<T, C> builder = new RouteBuilder<>();
        routeConfiguration.accept(builder);
        states.put(state, builder.buildForSource(description, state));
        return this;
    }

    public StateMachineBuilder<T, C> setInitial(@NonNull ProtocolState<T, C> initialState) {
        this.initialState = initialState;
        return this;
    }

    public StateMachineBuilder<T, C> addContext(@NonNull C context) {
        this.context = context;
        return this;
    }

    public StateMachineBuilder<T,C> addScheduler(ScheduledExecutorService service) {
        this.scheduler = service;
        return this;
    }

    public StateMachineBuilder<T, C> addConfiguration(@NonNull LoggerThingConfiguration configuration) {
        return this;
    }

    public StateMachine<T, C> build() {
        return new StateMachine<T, C>(states, initialState, context, scheduler);
    }

    public interface RouteConfigurer<T, C extends Context<T>> {
        RouteConfigurer<T, C> addNextRoute(ProtocolState<T, C> state);

        RouteConfigurer<T, C> addAlternativeRoute(ProtocolState<T, C> state);

        RouteConfigurer<T, C> addExceptionRoute(ProtocolState<T, C> state);

        RouteConfigurer<T, C> addErrorRoute(ProtocolState<T, C> state);
    }

    public static class RouteBuilder<T, C extends Context<T>> implements RouteConfigurer<T, C> {

        private ProtocolState<T, C> nextState;
        private ProtocolState<T, C> alternativeState;
        private ProtocolState<T, C> exceptionState;
        private ProtocolState<T, C> errorState;

        @Override
        public RouteConfigurer<T, C> addNextRoute(ProtocolState<T, C> state) {
            this.nextState = state;
            return this;
        }

        public ProtocolStateMeta<T, C> buildForSource(String description, ProtocolState<T, C> state) {
            return new ProtocolStateMetaImpl<T, C>(description, state, nextState, alternativeState, exceptionState, errorState);
        }

        @Override
        public RouteConfigurer<T, C> addAlternativeRoute(ProtocolState<T, C> state) {
            this.alternativeState = state;
            return this;
        }

        @Override
        public RouteConfigurer<T, C> addExceptionRoute(ProtocolState<T, C> state) {
            this.exceptionState = state;
            return this;
        }

        @Override
        public RouteConfigurer<T, C> addErrorRoute(ProtocolState<T, C> state) {
            this.errorState = state;
            return this;
        }

    }
}
