package ru.ccooll.rabbitclient.message;

import com.rabbitmq.client.AMQP;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;

public interface Message {

    AdaptedChannel channel();

    AMQP.BasicProperties properties();
}
