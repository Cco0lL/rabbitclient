package ru.ccooll.rabbitclient;

import ru.ccooll.rabbitclient.channel.AdaptedChannel;
import ru.ccooll.rabbitclient.common.Deserializer;
import ru.ccooll.rabbitclient.common.Serializer;

public interface Client extends AutoCloseable {

    AdaptedChannel createChannel();

    AdaptedChannel createChannel(Serializer serializer, Deserializer deserializer);
}


