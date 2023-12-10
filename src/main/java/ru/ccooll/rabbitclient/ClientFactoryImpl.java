package ru.ccooll.rabbitclient;

import com.google.common.base.Preconditions;
import com.rabbitmq.client.*;
import org.jetbrains.annotations.NotNull;
import ru.ccooll.rabbitclient.common.Converter;
import ru.ccooll.rabbitclient.common.simple.SimpleConverter;
import ru.ccooll.rabbitclient.error.ErrorHandler;

import java.util.concurrent.ExecutorService;

public class ClientFactoryImpl implements ClientFactory {

    private ConnectionFactory connectionFactory = new ConnectionFactory();
    private Converter converter = new SimpleConverter();
    private ErrorHandler errorHandler = Throwable::printStackTrace;

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
    public Client createNew(@NotNull String name, @NotNull ExecutorService clientWorker) {
        Preconditions.checkNotNull(name, "name is null");
        Preconditions.checkNotNull(clientWorker, "client worker is null");
        return new ClientImpl(connectionFactory, name, clientWorker, converter, errorHandler);
    }
}
