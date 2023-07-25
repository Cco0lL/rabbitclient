package ru.ccooll.rabbitclient;

import com.google.common.base.Preconditions;
import com.rabbitmq.client.ConnectionFactory;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import ru.ccooll.rabbitclient.common.Deserializer;
import ru.ccooll.rabbitclient.common.Serializer;
import ru.ccooll.rabbitclient.common.SimpleDeserializer;
import ru.ccooll.rabbitclient.common.SimpleSerializer;
import ru.ccooll.rabbitclient.error.ErrorHandler;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

public class ClientFactoryImpl implements ClientFactory {

    private ConnectionFactory connectionFactory = new ConnectionFactory();
    private Serializer serializer = new SimpleSerializer();
    private Deserializer deserializer = new SimpleDeserializer();
    private ErrorHandler errorHandler = (ex) -> { throw new RuntimeException(ex); };

    @Override
    public ClientFactory setConnectionFactory(@NotNull ConnectionFactory connectionFactory) {
        Preconditions.checkNotNull(connectionFactory, "connection factory");
        this.connectionFactory = connectionFactory;
        return this;
    }

    @Override
    public ClientFactory setDefaultSerializer(@NotNull Serializer serializer) {
        Preconditions.checkNotNull(serializer, "serializer is null");
        this.serializer = serializer;
        return this;
    }

    @Override
    public ClientFactory setDefaultDeserializer(@NotNull Deserializer deserializer) {
        Preconditions.checkNotNull(deserializer, "deserializer is null");
        this.deserializer = deserializer;
        return this;
    }

    @Override
    public ClientFactory setDefaultErrorHandler(@NotNull ErrorHandler handler) {
        Preconditions.checkNotNull(handler, "error handler is null");
        this.errorHandler = handler;
        return this;
    }

    @Override
    public Client createNewAndConnect(@NotNull String name, @NotNull ExecutorService clientWorker) throws IOException, TimeoutException {
        Preconditions.checkNotNull(name, "name is null");
        Preconditions.checkNotNull(clientWorker, "client worker is null");
        val connection = connectionFactory.newConnection(clientWorker, name);
        return new ClientImpl(connectionFactory, clientWorker, serializer, deserializer, errorHandler, connection);
    }
}
