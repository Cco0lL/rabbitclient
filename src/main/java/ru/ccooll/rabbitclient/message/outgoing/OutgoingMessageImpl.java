package ru.ccooll.rabbitclient.message.outgoing;

import com.rabbitmq.client.AMQP;
import org.jetbrains.annotations.NotNull;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;
import ru.ccooll.rabbitclient.message.incoming.IncomingMessage;
import ru.ccooll.rabbitclient.message.response.ResponseStrategy;

import java.util.concurrent.CompletableFuture;

public record OutgoingMessageImpl(AdaptedChannel channel, AMQP.BasicProperties properties,
                                  byte[] body) implements OutgoingMessage {

    @Override
    public boolean isRequestedResponse() {
        return channel.isQueueExist(properties.getReplyTo());
    }

    @Override
    public @NotNull <T> CompletableFuture<IncomingMessage<T>> responseRequest(@NotNull Class<T> rClass,
                                                                              @NotNull ResponseStrategy<T> strategy) {
        return strategy.processResponse(this, rClass);
    }
}