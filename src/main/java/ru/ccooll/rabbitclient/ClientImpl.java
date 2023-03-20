package ru.ccooll.rabbitclient;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;
import ru.ccooll.rabbitclient.channel.AdaptedChannelImpl;
import ru.ccooll.rabbitclient.common.Deserializer;
import ru.ccooll.rabbitclient.common.Serializer;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
public class ClientImpl implements Client {
    
    private final ConnectionFactory connectionFactory;
    private final ExecutorService clientWorker;
    private final Serializer defaultSerializer;
    private final Deserializer defaultDeserializer;
    private final String clientName;

    
    private Connection connection;
    
    @Override
    public void openConnection() throws IOException, TimeoutException {
        connection = connectionFactory.newConnection(clientWorker, clientName);
    }   

    @SneakyThrows
    @Override
    public @NotNull AdaptedChannel createChannel() {
        return new AdaptedChannelImpl(connection.createChannel(), defaultSerializer, defaultDeserializer);
    }

    @Override
    public void closeConnection() throws IOException {
        clientWorker.shutdown();
        connection.close();
    }
}
