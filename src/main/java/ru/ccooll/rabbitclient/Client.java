package ru.ccooll.rabbitclient;

import ru.ccooll.rabbitclient.channel.AdaptedChannel;
import ru.ccooll.rabbitclient.common.Converter;
import ru.ccooll.rabbitclient.error.ErrorHandler;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * represents client interface. client has only one connection
 * throughout the life cycle and unlimited amount of channels
 */
public interface Client extends AutoCloseable {

    String name();

    Converter converter();

    ErrorHandler errorHandler();

    void connect() throws IOException, TimeoutException;

    /**
     * creates channel with default serializers
     */
    AdaptedChannel createChannel();
}


