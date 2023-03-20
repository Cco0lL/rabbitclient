package ru.ccooll.rabbitclient.message.incoming;

import com.rabbitmq.client.AMQP;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ccooll.rabbitclient.RoutingData;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingMessage;
import lombok.val;

public record IncomingMessageImpl<T>(
        @NotNull AdaptedChannel channel,
        @NotNull AMQP.BasicProperties properties,
        @NotNull T message) implements IncomingMessage<T> {

    @Override
    public boolean isWaitingForResponse() {
        return channel.isQueueExist(properties.getReplyTo());
    }

    @Override
    public <R> @NotNull OutgoingMessage sendResponse(@Nullable R response) {
        val routingData = RoutingData.of(properties.getReplyTo());
        return channel.send(routingData, response);
    }
}
