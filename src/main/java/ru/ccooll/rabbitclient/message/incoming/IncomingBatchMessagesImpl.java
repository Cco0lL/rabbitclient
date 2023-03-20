package ru.ccooll.rabbitclient.message.incoming;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingBatchMessages;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingBatchMessagesImpl;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingMessage;

import java.util.List;
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
    public @NotNull <R> OutgoingBatchMessages sendBatchedResponse(@NotNull Function<IncomingMessage<T>, R> responseFunction) {
        List<OutgoingMessage> outgoingMessages = incomingMessages.stream()
                .map(it -> it.sendResponse(responseFunction.apply(it)))
                .toList();
        return new OutgoingBatchMessagesImpl(replyToKey, channel, outgoingMessages);
    }
}
