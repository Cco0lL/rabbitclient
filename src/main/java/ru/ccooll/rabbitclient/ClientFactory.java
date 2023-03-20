package ru.ccooll.rabbitclient;

import com.rabbitmq.client.ConnectionFactory;
import org.jetbrains.annotations.NotNull;
import ru.ccooll.rabbitclient.common.Deserializer;
import ru.ccooll.rabbitclient.common.Serializer;

import java.util.concurrent.ExecutorService;

public interface ClientFactory {

    static ClientFactory newInstance() {
        return new ClientFactoryImpl();
    }

    @NotNull ClientFactory setConnectionFactory(@NotNull ConnectionFactory connectionFactory);

    @NotNull ClientFactory setDefaultSerializer(@NotNull Serializer serializer);

    @NotNull ClientFactory setDefaultDeserializer(@NotNull Deserializer deserializer);

    @NotNull Client newClient(@NotNull String name, @NotNull ExecutorService clientWorker);
}
