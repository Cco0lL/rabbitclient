package ru.ccooll.rabbitclient;

import ru.ccooll.rabbitclient.channel.AdaptedChannel;
import ru.ccooll.rabbitclient.common.Converter;
import ru.ccooll.rabbitclient.error.ErrorHandler;

/**
 * represents client interface. client has only one connection
 * throughout the life cycle and unlimited amount of channels
 */
public interface Client extends AutoCloseable {

    /**
     * creates channel with default serializers
     */
    AdaptedChannel createChannel();

    Converter converter();

    ErrorHandler errorHandler();
}


