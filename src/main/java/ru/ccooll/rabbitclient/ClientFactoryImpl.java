package ru.ccooll.rabbitclient;

import com.google.common.base.Preconditions;
import com.rabbitmq.client.ConnectionFactory;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import ru.ccooll.rabbitclient.common.Converter;
import ru.ccooll.rabbitclient.common.simple.SimpleConverter;
import ru.ccooll.rabbitclient.connect.ClientConnectionStrategy;
import ru.ccooll.rabbitclient.error.ErrorHandler;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

public class ClientFactoryImpl implements ClientFactory {

    private ConnectionFactory connectionFactory = new ConnectionFactory();
    private Converter converter = new SimpleConverter();
    private ErrorHandler errorHandler = (ex) -> {
        throw new IllegalStateException(ex);
    };
    private ClientConnectionStrategy clientConnectionStrategy;

    @Override
    public ClientFactory setConnectionFactory(@NotNull ConnectionFactory connectionFactory) {
        Preconditions.checkNotNull(connectionFactory, "connection factory");
        this.connectionFactory = connectionFactory;
        return this;
    }

    @Override
    public ClientFactory setDefaultConverter(@NotNull Converter converter) {
        Preconditions.checkNotNull(converter);
        this.converter = converter;
        return this;
    }

    @Override
    public ClientFactory setDefaultErrorHandler(@NotNull ErrorHandler handler) {
        Preconditions.checkNotNull(handler, "error handler is null");
        this.errorHandler = handler;
        return this;
    }

    @Override
    public ClientFactory setClientConnectionStrategy(
            @NotNull ClientConnectionStrategy clientConnectionStrategy) {
        Preconditions.checkNotNull(clientConnectionStrategy, "client connection is null");
        this.clientConnectionStrategy = clientConnectionStrategy;
        return this;
    }

    @Override
    public Client createNewAndConnect(@NotNull String name, @NotNull ExecutorService clientWorker) throws IOException, TimeoutException {
        Preconditions.checkNotNull(name, "name is null");
        Preconditions.checkNotNull(clientWorker, "client worker is null");

        if (clientConnectionStrategy != null) {
            return errorHandler.computeSafe(() -> clientConnectionStrategy.connect(name, this, clientWorker));
        } else {
            val connection = connectionFactory.newConnection(clientWorker, name);
            return new ClientImpl(connection, clientWorker, converter, errorHandler);
        }
    }
}
