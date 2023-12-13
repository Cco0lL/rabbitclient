package ru.ccooll.rabbitclient.message.outgoing;

import lombok.val;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;
import ru.ccooll.rabbitclient.message.Message;
import ru.ccooll.rabbitclient.message.incoming.IncomingBatchMessage;
import ru.ccooll.rabbitclient.message.incoming.IncomingMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * represents interface that able to request response
 * from receiver
 */
public interface Outgoing extends Message {

    void setSenderChannel(AdaptedChannel sender);

    void markRequestedResponse();

    boolean isRequestedResponse();

    default <T> CompletableFuture<IncomingMessage<T>> responseRequest(Class<T> rClass) {
        if (isRequestedResponse()) {
            throw new IllegalStateException("response already requested");
        }
        val channel = channel();
        if (channel == null) {
            throw new IllegalStateException("Message wasn't sent");
        }

        CompletableFuture<IncomingMessage<T>> future = new CompletableFuture<>();
        val properties = properties();

        val replyTo = properties.getReplyTo();
        channel.declareQueue(replyTo, true);
        val ct = channel.addConsumer(replyTo, rClass, future::complete);
        markRequestedResponse();

        return future.thenApply(it -> {
            channel.removeConsumer(ct);
            channel.removeQueue(replyTo);
            return it;
        });
    }

    default <T> CompletableFuture<IncomingBatchMessage<T>> responseRequestBatch(Class<T> rClass) {
        if (isRequestedResponse()) {
            throw new IllegalStateException("response already requested");
        }
        val channel = channel();
        if (channel == null) {
            throw new IllegalStateException("Message wasn't sent");
        }

        List<T> messages = Collections.synchronizedList(new ArrayList<>());
        CompletableFuture<IncomingBatchMessage<T>> future = new CompletableFuture<>();

        val properties = properties();
        channel.declareQueue(properties.getReplyTo(), true);

        val ct = channel.addConsumer(properties.getReplyTo(), false, rClass, mes -> {
            messages.add(mes.message());
            val incomingProperties = mes.properties();
            val headers = incomingProperties.getHeaders();
            if (headers == null) {
                return;
            }
            //value is boolean, always true if this header exists
            if (headers.containsKey(OutgoingBatchMessage.END_BATCH_POINTER)) {
                val deliveryTag = mes.deliveryTag();
                future.complete(new IncomingBatchMessage<>(deliveryTag, channel, incomingProperties,
                        new ArrayList<>(messages)));
                channel.ack(deliveryTag, true);
            }
        });
        markRequestedResponse();

        return future.thenApply(it -> {
            channel.removeConsumer(ct);
            channel.removeQueue(properties.getReplyTo());
            return it;
        });
    }
}
