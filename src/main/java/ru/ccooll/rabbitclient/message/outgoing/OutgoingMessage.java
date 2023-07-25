package ru.ccooll.rabbitclient.message.outgoing;

import com.rabbitmq.client.AMQP;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;
import ru.ccooll.rabbitclient.message.incoming.Incoming;

import java.util.concurrent.CompletableFuture;

public record OutgoingMessage(AdaptedChannel channel,
                              AMQP.BasicProperties properties) implements Outgoing {

    @Override
    public boolean isRequestedResponse() {
        return channel.isQueueExist(properties.getReplyTo());
    }
}