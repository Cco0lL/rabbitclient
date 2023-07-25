package ru.ccooll.rabbitclient;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;
import ru.ccooll.rabbitclient.channel.AdaptedChannelImpl;
import ru.ccooll.rabbitclient.common.Deserializer;
import ru.ccooll.rabbitclient.common.Serializer;
import ru.ccooll.rabbitclient.error.ErrorHandler;

import java.util.concurrent.ExecutorService;


public record ClientImpl(ConnectionFactory connectionFactory, ExecutorService clientWorker,
                         Serializer defaultSerializer, Deserializer defaultDeserializer,
                         ErrorHandler errorHandler, Connection connection) implements Client {

    @Override
    public AdaptedChannel createChannel() {
        return errorHandler.computeSafe(() ->
                new AdaptedChannelImpl(connection.createChannel(), defaultSerializer,
                        defaultDeserializer, errorHandler));
    }

    @Override
    public AdaptedChannel createChannel(Serializer serializer, Deserializer deserializer) {
        return errorHandler.computeSafe(() ->
                new AdaptedChannelImpl(connection.createChannel(), serializer, deserializer,
                        errorHandler));
    }

    @Override
    public void close() {
        clientWorker.shutdown();
        errorHandler.computeSafe(() -> connection.close());
    }
}
