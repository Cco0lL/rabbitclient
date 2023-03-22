package ru.ccooll.rabbitclient.message.incoming;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import ru.ccooll.rabbitclient.RoutingData;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingBatchMessages;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingBatchMessagesImpl;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingMessage;
import ru.ccooll.rabbitclient.util.MessagePropertiesUtils;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public record IncomingBatchMessagesImpl<T>(
        String replyToKey,
        AdaptedChannel channel,
        List<IncomingMessage<T>> incomingMessages) implements IncomingBatchMessages<T> {

    @Override
    public boolean isWaitingForResponse() {
        return channel.isQueueExist(replyToKey);
    }

    @Override
    public @NotNull <R> OutgoingBatchMessages sendBatchResponse(@NotNull Function<IncomingMessage<T>, R> responseFunction) {
        val replyTo = UUID.randomUUID().toString();
        List<OutgoingMessage> outgoingMessages = incomingMessages.stream()
                .map(it -> {
                     val message = responseFunction.apply(it);
                     val incomingProperties = it.properties();
                     val outgoingProperties = MessagePropertiesUtils.create(incomingProperties.getCorrelationId(), replyTo);
                     return channel.send(RoutingData.of(replyToKey), message, outgoingProperties);
                }).toList();
        return new OutgoingBatchMessagesImpl(replyToKey, channel, outgoingMessages);
    }
}
