package ru.ccooll.rabbitclient;

import com.rabbitmq.client.ConnectionFactory;
import org.jetbrains.annotations.NotNull;
import ru.ccooll.rabbitclient.common.Converter;
import ru.ccooll.rabbitclient.error.ErrorHandler;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

/**
 * represents client factory
 */
public interface ClientFactory {

    /**
     * fabric method
     *
     * @return new instance
     */
    static ClientFactory newInstance() {
        return new ClientFactoryImpl();
    }

    /**
     * sets connection factory with client properties
     *
     * @param connectionFactory - built-in rabbit connection factory
     * @return this
     */
    ClientFactory setConnectionFactory(@NotNull ConnectionFactory connectionFactory);

    /**
     * sets default to/from bytes converter
     */
    ClientFactory setDefaultConverter(@NotNull Converter converter);

    /**
     * sets default error handler, by default implements sneaky throws conception
     *
     * @param handler - error handler
     * @return this
     */
    ClientFactory setDefaultErrorHandler(@NotNull ErrorHandler handler);

    /**
     * creates new rabbit client
     *
     * @param name         - client name
     * @param clientWorker - executor service for consumers on this client
     * @return this
     */
    Client createNew(@NotNull String name, @NotNull ExecutorService clientWorker);

    Client createNewAndConnect(@NotNull String name,
                               @NotNull ExecutorService clientWorker)
            throws IOException, TimeoutException;
}
