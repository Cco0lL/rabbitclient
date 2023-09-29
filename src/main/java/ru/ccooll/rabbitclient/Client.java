package ru.ccooll.rabbitclient;

import ru.ccooll.rabbitclient.channel.AdaptedChannel;

/**
 * represents client interface. client has only one connection
 * throughout the life cycle and unlimited amount of channels
 */
public interface Client extends AutoCloseable {

    /**
     * creates channel with default serializers
     * @return this
     */
    AdaptedChannel createChannel();
}


