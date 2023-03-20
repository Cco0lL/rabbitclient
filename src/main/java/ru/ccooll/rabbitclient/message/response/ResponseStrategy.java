package ru.ccooll.rabbitclient.message.response;

import com.rabbitmq.client.Delivery;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import ru.ccooll.rabbitclient.message.incoming.IncomingMessage;
import ru.ccooll.rabbitclient.message.incoming.IncomingMessageImpl;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingBatchMessages;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface ResponseStrategy<T> {

    static <T> ResponseStrategy<T> single() {
        return new SingleResponseStrategy<>();
    }

    static <T> BatchResponseStrategy<T> batch(@NotNull OutgoingBatchMessages batchMessages) {
        return new BatchResponseStrategy.BatchResponseStrategyImpl<>(batchMessages);
    }

    CompletableFuture<IncomingMessage<T>> processResponse(OutgoingMessage message, Class<T> rClass);

    class SingleResponseStrategy<T> implements ResponseStrategy<T> {

        @Override
        public CompletableFuture<IncomingMessage<T>> processResponse(OutgoingMessage message,
                                                                     Class<T> rClass) {
            val channel = message.channel();

            val replyTo = message.properties().getReplyTo();
            channel.declareQueue(replyTo);

            val future = new CompletableFuture<IncomingMessage<T>>();

            val consumerTag = channel.addConsumer(replyTo, (tag, incoming) -> {
                val incomingProperties = incoming.getProperties();
                if (incomingProperties.getCorrelationId().equals(incomingProperties.getCorrelationId())) {
                    val fromBytes = channel.deserializer().deserialize(incoming.getBody(), rClass);
                    val incomingMessage = new IncomingMessageImpl<>(channel, incomingProperties, fromBytes);
                    future.complete(incomingMessage);
                }
            });

            return future.thenApply(it -> {
                channel.removeConsumer(consumerTag);
                return it;
            });
        }
    }

    interface BatchResponseStrategy<T> extends ResponseStrategy<T> {

        @NotNull List<@NotNull IncomingMessage<T>> incomingMessages();

        void startConsume(@NotNull String replyTo);

        boolean isCompleted();

        @Getter
        @RequiredArgsConstructor
        @Accessors(fluent = true)
        class BatchResponseStrategyImpl<T> implements BatchResponseStrategy<T> {

            private final List<IncomingMessage<T>> incomingMessages = new ArrayList<>();
            private final Map<String, CompletableFuture<Delivery>> futuresMap = new HashMap<>();
            private final OutgoingBatchMessages batchMessages;

            @Override
            public CompletableFuture<IncomingMessage<T>> processResponse(OutgoingMessage message,
                                                                         Class<T> responseClass) {
                val channel = batchMessages.channel();
                val correlationId = message.properties().getCorrelationId();
                CompletableFuture<Delivery> future = new CompletableFuture<>();

                futuresMap.put(correlationId, future);

                return future.thenApply(it -> {
                    val fromBytes = channel.deserializer().deserialize(it.getBody(), responseClass);
                    val incomingMessage = new IncomingMessageImpl<>(channel, it.getProperties(), fromBytes);

                    incomingMessages.add(incomingMessage);
                    futuresMap.remove(correlationId);

                    return incomingMessage;
                });
            }

            @Override
            public void startConsume(@NotNull String replyTo) {
                batchMessages.channel().addConsumer(replyTo, ((consumerTag, message) -> {
                    val future = futuresMap.get(message.getProperties().getCorrelationId());
                    future.complete(message);
                }));
            }

            @Override
            public boolean isCompleted() {
                return futuresMap.isEmpty();
            }
        }
    }
}
