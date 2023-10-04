package ru.ccooll.rabbitclient.connect;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import ru.ccooll.rabbitclient.Client;
import ru.ccooll.rabbitclient.ClientFactory;

import java.util.concurrent.ExecutorService;

@Slf4j
public class AutoReconnectStrategy implements ClientConnectionStrategy {

    private static final int DEFAULT_RECONNECT_ATTEMPTS = 10;
    private static final long DEFAULT_RECONNECT_DELAY_MS = 1000L;

    private final int reconnectAttempts;
    private final long reconnectDelay;

    private int currentReconnectAttempts = 0;

    public AutoReconnectStrategy() {
        this(DEFAULT_RECONNECT_ATTEMPTS, DEFAULT_RECONNECT_DELAY_MS);
    }

    public AutoReconnectStrategy(Config config) {
        this(config.getInt("attempts"), config.getLong("delay"));
    }

    public AutoReconnectStrategy(int reconnectAttempts, long reconnectDelay) {
        this.reconnectAttempts = reconnectAttempts;
        this.reconnectDelay = reconnectDelay;
    }

    @Override
    public Client connect(String name, ClientFactory clientFactory,
                          ExecutorService clientWorker) throws Exception {
        try {
            return clientFactory.createNewAndConnect(name, clientWorker);
        } catch (Throwable th) {
            return tryReconnect(name, clientFactory, clientWorker);
        }
    }

    private Client tryReconnect(String name, ClientFactory clientFactory,
                                ExecutorService clientWorker) throws Exception {
        //TODO change on logger
        String message = String.format("An error occurred on client connection," +
                        " trying to connect again, attempt: %d",
                ++currentReconnectAttempts);
        System.out.println(message);

        if (currentReconnectAttempts == reconnectAttempts) {
            clientWorker.close();
            throw new Exception("Connection failed since all allowed reconnect attempts");
        }

        try {
            Thread.sleep(reconnectDelay);
        } catch (InterruptedException e) {
            clientWorker.close();
            throw new Exception("connection failed");
        }

        return connect(name, clientFactory, clientWorker);
    }
}
