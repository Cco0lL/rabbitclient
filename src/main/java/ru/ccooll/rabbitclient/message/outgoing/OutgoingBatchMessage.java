package ru.ccooll.rabbitclient.message.outgoing;

import com.rabbitmq.client.AMQP;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;

public record OutgoingBatchMessage(
        AdaptedChannel channel,
        AMQP.BasicProperties properties) implements Outgoing {

    @Override
    public boolean isRequestedResponse() {
        return channel.isQueueExist(properties.getReplyTo());
    }
}
