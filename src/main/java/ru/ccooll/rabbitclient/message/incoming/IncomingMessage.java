package ru.ccooll.rabbitclient.message.incoming;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ccooll.rabbitclient.message.Incoming;
import ru.ccooll.rabbitclient.message.Message;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingMessage;

public interface IncomingMessage<T> extends Message, Incoming {

    @NotNull T message();

    <R> @NotNull OutgoingMessage sendResponse(@Nullable R response);
}
