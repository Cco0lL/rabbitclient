package ru.ccooll.rabbitclient.message.incoming;

import com.rabbitmq.client.AMQP;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;

import java.util.List;

public record IncomingBatchMessage<T>(
        AdaptedChannel channel,
        AMQP.BasicProperties properties,
        List<T> message) implements Incoming<List<T>> {

    @Override
    public boolean isWaitingForResponse() {
        return channel.isQueueExist(properties.getReplyTo());
    }
}
