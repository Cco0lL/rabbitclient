package ru.ccooll.rabbitclient;

import com.rabbitmq.client.ConnectionFactory;
import org.jetbrains.annotations.NotNull;
import ru.ccooll.rabbitclient.common.Deserializer;
import ru.ccooll.rabbitclient.common.Serializer;

import java.util.concurrent.ExecutorService;

public class ClientFactoryImpl implements ClientFactory {

    private ConnectionFactory connectionFactory;
    private Serializer serializer;
    private Deserializer deserializer;

    @Override
    public @NotNull ClientFactory setConnectionFactory(@NotNull ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        return this;
    }

    @Override
    public @NotNull ClientFactory setDefaultSerializer(@NotNull Serializer serializer) {
        this.serializer = serializer;
        return this;
    }

    @Override
    public @NotNull ClientFactory setDefaultDeserializer(@NotNull Deserializer deserializer) {
        this.deserializer = deserializer;
        return this;
    }

    @Override
    public @NotNull Client newClient(@NotNull String name, @NotNull ExecutorService clientWorker) {
        return null;
    }
}
