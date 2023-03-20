package ru.ccooll.rabbitclient.message.incoming;

import org.jetbrains.annotations.NotNull;
import ru.ccooll.rabbitclient.message.BatchMessages;
import ru.ccooll.rabbitclient.message.Incoming;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingBatchMessages;

import java.util.List;
import java.util.function.Function;

public interface IncomingBatchMessages<T> extends BatchMessages, Incoming {

    @NotNull List<@NotNull IncomingMessage<T>> incomingMessages();

    @NotNull <R> OutgoingBatchMessages sendBatchedResponse(
            @NotNull Function<IncomingMessage<T>, R> responseFunction);
}
