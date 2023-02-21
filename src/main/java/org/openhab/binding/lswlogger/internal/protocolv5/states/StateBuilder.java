package org.openhab.binding.lswlogger.internal.protocolv5.states;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.openhab.binding.lswlogger.internal.LoggerThingConfiguration;
import org.openhab.binding.lswlogger.internal.connection.Context;

public class StateBuilder<C extends Context> {

    private final Map<ProtocolState<C>, ProtocolStateMeta<C>> states = new HashMap<>();
    private C context;
    private LoggerThingConfiguration configuration;
    private ProtocolState<C> initialState;

    public StateBuilder<C> addState(ProtocolState<C> state, Consumer<RouteConfigurer<C>> routeConfiguration) {
        RouteBuilder<C> builder = new RouteBuilder<>();
        routeConfiguration.accept(builder);
        states.put(state, builder.buildForSource(state));
        return this;
    }

    public StateBuilder<C> setInitial(ProtocolState<C> initialState) {
        this.initialState = initialState;
        return this;
    }

    public StateBuilder<C> addContext(C context) {
        this.context = context;
        return this;
    }

    public StateBuilder<C> addConfiguration(LoggerThingConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    public StateMachine<C> build() {
        return new StateMachine<C>(context, configuration, states, initialState);
    }

    public interface RouteConfigurer<C extends Context> {
        RouteConfigurer<C> addNextRoute(ProtocolState<C> state);

        RouteConfigurer<C> addAlternativeRoute(ProtocolState<C> state);

        RouteConfigurer<C> addExceptionRoute(ProtocolState<C> state);

        RouteConfigurer<C> addErrorRoute(ProtocolState<C> state);
    }

    public static class RouteBuilder<C extends Context> implements RouteConfigurer<C> {

        private ProtocolState<C> nextState;
        private ProtocolState<C> alternativeState;
        private ProtocolState<C> exceptionState;
        private ProtocolState<C> errorState;

        @Override
        public RouteConfigurer<C> addNextRoute(ProtocolState<C> state) {
            this.nextState = state;
            return this;
        }

        public ProtocolStateMeta<C> buildForSource(ProtocolState<C> state) {
            return new ProtocolStateMeta<C>(state, nextState, alternativeState, exceptionState, errorState);
        }

        @Override
        public RouteConfigurer<C> addAlternativeRoute(ProtocolState<C> state) {
            this.alternativeState = state;
            return this;
        }

        @Override
        public RouteConfigurer<C> addExceptionRoute(ProtocolState<C> state) {
            this.exceptionState = state;
            return this;
        }

        @Override
        public RouteConfigurer<C> addErrorRoute(ProtocolState<C> state) {
            this.errorState = state;
            return this;
        }

    }
}
