package ru.ccooll.rabbitclient;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;
import ru.ccooll.rabbitclient.channel.AdaptedChannelImpl;
import ru.ccooll.rabbitclient.common.Converter;
import ru.ccooll.rabbitclient.error.ErrorHandler;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
public class ClientImpl implements Client {

    private final ConnectionFactory connectionFactory;
    private final String name;
    private final ExecutorService clientWorker;
    private final Converter converter;
    private final ErrorHandler errorHandler;
    private Connection connection;

    @Override
    public String name() {
        return name;
    }

    @Override
    public Converter converter() {
        return converter;
    }

    @Override
    public ErrorHandler errorHandler() {
        return errorHandler;
    }

    @Override
    public void connect() throws IOException, TimeoutException {
        if (connection != null) {
            throw new IOException("Client is already connected");
        }
        connection = connectionFactory.newConnection(clientWorker, name);
    }

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
