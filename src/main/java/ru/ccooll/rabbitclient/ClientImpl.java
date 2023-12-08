package ru.ccooll.rabbitclient;

import com.rabbitmq.client.Connection;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;
import ru.ccooll.rabbitclient.channel.AdaptedChannelImpl;
import ru.ccooll.rabbitclient.common.Converter;
import ru.ccooll.rabbitclient.error.ErrorHandler;

import java.util.concurrent.ExecutorService;

public record ClientImpl(Connection connection, ExecutorService clientWorker,
                         Converter converter, ErrorHandler errorHandler) implements Client {

    @Override
    public AdaptedChannel createChannel() {
        return errorHandler.computeSafe(() ->
                new AdaptedChannelImpl(connection.createChannel(), converter, errorHandler));
    }

    @Override
    public void close() {
        clientWorker.shutdown();
        errorHandler.computeSafe(() -> connection.close());
    }
}
