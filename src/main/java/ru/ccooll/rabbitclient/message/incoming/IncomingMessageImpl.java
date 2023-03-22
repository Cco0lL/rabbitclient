package ru.ccooll.rabbitclient.message.incoming;

import com.rabbitmq.client.AMQP;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ccooll.rabbitclient.RoutingData;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingMessage;
import lombok.val;
import ru.ccooll.rabbitclient.util.MessagePropertiesUtils;

public record IncomingMessageImpl<T>(
        @NotNull AdaptedChannel channel,
        @NotNull AMQP.BasicProperties properties,
        @NotNull T message) implements IncomingMessage<T> {

    @Override
    public boolean isWaitingForResponse() {
        return channel.isQueueExist(properties.getReplyTo());
    }

    @Override
    public <R> @NotNull OutgoingMessage sendResponse(@NotNull R response) {
        val replyTo = properties.getReplyTo();
        val routingData = RoutingData.of(replyTo);
        val responseProperties = MessagePropertiesUtils.createWithCorrelationId(properties.getCorrelationId());
        return channel.send(routingData, response, responseProperties);
    }
}
