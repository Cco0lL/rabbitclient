package ru.ccooll.rabbitclient.message.outgoing;

import org.jetbrains.annotations.NotNull;
import ru.ccooll.rabbitclient.message.Message;
import ru.ccooll.rabbitclient.message.Outgoing;
import ru.ccooll.rabbitclient.message.incoming.IncomingMessage;
import ru.ccooll.rabbitclient.message.response.ResponseStrategy;

import java.util.concurrent.CompletableFuture;

public interface OutgoingMessage extends Message, Outgoing {

    byte @NotNull [] body();

    @NotNull <T> CompletableFuture<IncomingMessage<T>> responseRequest(@NotNull Class<T> rClass,
                                                                       @NotNull ResponseStrategy<T> strategy);
}
