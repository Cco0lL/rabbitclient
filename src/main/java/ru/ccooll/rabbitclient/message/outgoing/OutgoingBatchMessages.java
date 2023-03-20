package ru.ccooll.rabbitclient.message.outgoing;

import org.jetbrains.annotations.NotNull;
import ru.ccooll.rabbitclient.message.BatchMessages;
import ru.ccooll.rabbitclient.message.Outgoing;
import ru.ccooll.rabbitclient.message.incoming.IncomingBatchMessages;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface OutgoingBatchMessages extends BatchMessages, Outgoing {

    @NotNull List<@NotNull OutgoingMessage> outgoingMessages();

    @NotNull <T> CompletableFuture<IncomingBatchMessages<T>> responseRequest(@NotNull Class<T> rClass);
}
