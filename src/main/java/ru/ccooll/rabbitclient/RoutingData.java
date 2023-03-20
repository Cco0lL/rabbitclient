package ru.ccooll.rabbitclient;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface RoutingData {

    static RoutingData of(@NotNull String routingKey) {
        return new RoutingDataImpl("", routingKey);
    }

    static RoutingData of(@NotNull String exchange, @NotNull String routingKey) {
        return new RoutingDataImpl(exchange, routingKey);
    }

    @NotNull String exchange();

    @NotNull String routingKey();

    @Contract(pure = true)
    @NotNull RoutingData exchange(@NotNull String exchange);

    @Contract(pure = true)
    @NotNull RoutingData routingKey(@NotNull String routingKey);

    record RoutingDataImpl(@NotNull String exchange,
                           @NotNull String routingKey) implements RoutingData {

        @Override
        public @NotNull RoutingData exchange(@NotNull String exchange) {
            return new RoutingDataImpl(exchange, routingKey);
        }

        @Override
        public @NotNull RoutingData routingKey(@NotNull String routingKey) {
            return new RoutingDataImpl(exchange, routingKey);
        }
    }
}
