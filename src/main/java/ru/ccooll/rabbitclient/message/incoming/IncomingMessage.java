package ru.ccooll.rabbitclient.message.incoming;

import com.rabbitmq.client.AMQP;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;

public record IncomingMessage<T>(
        AdaptedChannel channel,
        AMQP.BasicProperties properties,
        T message) implements Incoming<T> {

    @Override
    public boolean isWaitingForResponse() {
        return channel.isQueueExist(properties.getReplyTo());
    }
}
