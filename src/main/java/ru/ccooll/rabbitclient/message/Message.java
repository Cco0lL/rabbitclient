package ru.ccooll.rabbitclient.message;

import com.rabbitmq.client.AMQP;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;

/**
 * represents message interface
 */
public interface Message {

    /**
     * @return channel of message scope
     */
    AdaptedChannel channel();

    /**
     * @return - message properties
     */
    AMQP.BasicProperties properties();
}
