package ru.ccooll.rabbitclient.connect;

import ru.ccooll.rabbitclient.Client;
import ru.ccooll.rabbitclient.ClientFactory;

import java.util.concurrent.ExecutorService;

public interface ClientConnectionStrategy {

    Client connect(String name, ClientFactory clientFactory,
                   ExecutorService clientWorker) throws Exception;
}
