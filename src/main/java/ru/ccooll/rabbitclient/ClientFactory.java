package ru.ccooll.rabbitclient;

import com.rabbitmq.client.ConnectionFactory;
import org.jetbrains.annotations.NotNull;
import ru.ccooll.rabbitclient.common.Deserializer;
import ru.ccooll.rabbitclient.common.Serializer;
import ru.ccooll.rabbitclient.error.ErrorHandler;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

public interface ClientFactory {

    static ClientFactory newInstance() {
        return new ClientFactoryImpl();
    }

    ClientFactory setConnectionFactory(@NotNull ConnectionFactory connectionFactory);

    ClientFactory setDefaultSerializer(@NotNull Serializer serializer);

    ClientFactory setDefaultDeserializer(@NotNull Deserializer deserializer);

    ClientFactory setDefaultErrorHandler(@NotNull ErrorHandler handler);

    Client createNewAndConnect(@NotNull String name, @NotNull ExecutorService clientWorker)
            throws IOException, TimeoutException;
}
