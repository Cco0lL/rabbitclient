package ru.ccooll.rabbitclient.util;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * represents exchange key and routing key pair
 */
public interface RoutingData {

    static RoutingData of(@NotNull String exchange, @NotNull String routingKey) {
        Preconditions.checkNotNull(exchange, "exchange is null");
        Preconditions.checkNotNull(routingKey, "routing key is null");
        return new RoutingDataImpl(exchange, routingKey);
    }

    static RoutingData of(@NotNull String routingKey) {
        return of("", routingKey);
    }

    String exchange();

    String routingKey();

    @Contract(pure = true)
    RoutingData withExchange(@NotNull String exchange);

    @Contract(pure = true)
    RoutingData withRoutingKey(@NotNull String routingKey);

    record RoutingDataImpl(@NotNull String exchange,
                           @NotNull String routingKey) implements RoutingData {

        @Override
        public RoutingData withExchange(@NotNull String exchange) {
            return RoutingData.of(exchange, routingKey);
        }

        @Override
        public RoutingData withRoutingKey(@NotNull String routingKey) {
            return RoutingData.of(exchange, routingKey);
        }
    }
}
