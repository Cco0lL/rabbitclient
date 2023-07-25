package ru.ccooll.rabbitclient.message.outgoing;

import com.rabbitmq.client.AMQP;
import lombok.val;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;
import ru.ccooll.rabbitclient.message.Message;
import ru.ccooll.rabbitclient.message.incoming.IncomingBatchMessage;
import ru.ccooll.rabbitclient.message.incoming.IncomingMessage;
import ru.ccooll.rabbitclient.util.MessagePropertiesUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Outgoing extends Message {

    boolean isRequestedResponse();

    default <T> CompletableFuture<IncomingMessage<T>> responseRequest(Class<T> rClass) {
        CompletableFuture<IncomingMessage<T>> future = new CompletableFuture<>();

        val channel = channel();
        val properties = properties();

        CallbackConsumer consumer = (deliveryTag, incomingProperties, body) -> {
            val errorHandler = channel.errorHandler();
            val deserializer = channel.deserializer();
            val message = errorHandler.computeSafe(() -> deserializer.deserialize(body, rClass));
            future.complete(new IncomingMessage<T>(channel, incomingProperties, message));
            channel.ack(deliveryTag, false);
        };
        val ct = consumer.consume(channel, properties);

        return future.thenApply(it -> {
            channel.removeConsumer(ct);
            return it;
        });
    }

    default <T> CompletableFuture<IncomingBatchMessage<T>> responseRequestBatch(Class<T> rClass) {
        List<T> messages = Collections.synchronizedList(new ArrayList<>());
        CompletableFuture<IncomingBatchMessage<T>> future = new CompletableFuture<>();

        val properties = properties();
        val channel = channel();

        CallbackConsumer consumer = (deliveryTag, incomingProperties, body) -> {
            val errorHandler = channel.errorHandler();
            val deserializer = channel.deserializer();
            val message = errorHandler.computeSafe(() -> deserializer.deserialize(body, rClass));
            messages.add(message);

            val headers = incomingProperties.getHeaders();
            //value is boolean, always true if this header exists
            if (headers.containsKey(MessagePropertiesUtils.END_BATCH_POINTER)) {
                future.complete(new IncomingBatchMessage<>(channel, incomingProperties,
                        new ArrayList<>(messages)));
                channel.ack(deliveryTag, true);
            }
        };
        val ct = consumer.consume(channel, properties);

        return future.thenApply(it -> {
            channel.removeConsumer(ct);
            return it;
        });
    }

    @FunctionalInterface
    interface CallbackConsumer {

        void handleDelivery(long deliveryTag, AMQP.BasicProperties properties, byte[] body);

        default String consume(AdaptedChannel channel, AMQP.BasicProperties properties) {
            return channel.addConsumer(properties.getReplyTo(), false, ((consumerTag, delivery) -> {
                val incomingProperties = delivery.getProperties();
                if (incomingProperties.getCorrelationId().equals(properties.getCorrelationId())) {
                    val envelope = delivery.getEnvelope();
                    handleDelivery(envelope.getDeliveryTag(), incomingProperties, delivery.getBody());
                }
            }));
        }
    }
}
