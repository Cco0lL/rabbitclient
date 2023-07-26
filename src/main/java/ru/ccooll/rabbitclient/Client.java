package ru.ccooll.rabbitclient;

import org.jetbrains.annotations.NotNull;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;
import ru.ccooll.rabbitclient.common.Deserializer;
import ru.ccooll.rabbitclient.common.Serializer;

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

    /**
     * creates channel with specific serializers
     * @param serializer - serializer
     * @param deserializer - deserializer
     * @return this
     */
    AdaptedChannel createChannel(@NotNull Serializer serializer,
                                 @NotNull Deserializer deserializer);
}


