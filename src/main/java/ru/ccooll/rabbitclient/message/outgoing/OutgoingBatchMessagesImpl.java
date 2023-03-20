package ru.ccooll.rabbitclient.message.outgoing;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;
import ru.ccooll.rabbitclient.message.incoming.IncomingBatchMessages;
import ru.ccooll.rabbitclient.message.incoming.IncomingBatchMessagesImpl;
import ru.ccooll.rabbitclient.message.response.ResponseStrategy;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public record OutgoingBatchMessagesImpl(
        String replyToKey,
        AdaptedChannel channel,
        List<OutgoingMessage> outgoingMessages) implements OutgoingBatchMessages {

    @Override
    public boolean isRequestedResponse() {
        return channel.isQueueExist(replyToKey);
    }

    @SuppressWarnings({"StatementWithEmptyBody"})
    @Override
    public @NotNull <T> CompletableFuture<IncomingBatchMessages<T>> responseRequest(@NotNull Class<T> rClass) {
        val responseStrategy = ResponseStrategy.<T>batch(this);
        outgoingMessages.forEach(it -> it.responseRequest(rClass, responseStrategy));

        val future = new CompletableFuture<IncomingBatchMessages<T>>();

        Thread.startVirtualThread(() -> {
            while (!responseStrategy.isCompleted());
            future.complete(new IncomingBatchMessagesImpl<>(replyToKey, channel, responseStrategy.incomingMessages()));
        });

        return future.thenApply(it -> {
            channel.removeConsumer(replyToKey);
            return it;
        });
    }
}
