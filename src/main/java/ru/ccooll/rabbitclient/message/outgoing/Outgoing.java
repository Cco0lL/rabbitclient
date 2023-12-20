package ru.ccooll.rabbitclient.message.outgoing;

import lombok.val;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;
import ru.ccooll.rabbitclient.message.Message;
import ru.ccooll.rabbitclient.message.incoming.IncomingBatchMessage;
import ru.ccooll.rabbitclient.message.incoming.IncomingMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * represents interface that able to request response
 * from receiver
 */
public interface Outgoing extends Message {

    void setSenderChannel(AdaptedChannel sender);

    void markRequestedResponse();

    boolean isRequestedResponse();

    default <T> IncomingMessage<T> responseRequest(Class<T> rClass) {
        return responseRequest(rClass, -1, TimeUnit.MILLISECONDS);
    }

    default <T> IncomingMessage<T> responseRequest(Class<T> rClass, long waitTime, TimeUnit timeUnit) {
        ResponseConsumer<T, IncomingMessage<T>> responseConsumer =
                (rclass, message, forComplete) -> forComplete.complete(message);
        return responseConsumer.consumeResponse(rClass, this, true, waitTime, timeUnit);
    }

    default <T> IncomingBatchMessage<T> responseRequestBatch(Class<T> rClass) {
        return responseRequestBatch(rClass, -1, TimeUnit.MILLISECONDS);
    }

    default <T> IncomingBatchMessage<T> responseRequestBatch(Class<T> rClass, long waitTime, TimeUnit timeUnit) {
        List<T> messages = new ArrayList<>();
        ResponseConsumer<T, IncomingBatchMessage<T>> responseConsumer = (rclass, message, forComplete) -> {
            messages.add(message.message());
            val incomingProperties = message.properties();
            val headers = incomingProperties.getHeaders();
            if (headers == null) {
                return;
            }
            //value is boolean, always true if this header exists
            if (headers.containsKey(OutgoingBatchMessage.END_BATCH_POINTER)) {
                val envelope = message.envelope();
                AdaptedChannel channel = message.channel();
                forComplete.complete(new IncomingBatchMessage<>(channel, envelope, incomingProperties,
                        messages));
                channel.ack(envelope.getDeliveryTag(), true);
            }
        };
        return responseConsumer.consumeResponse(rClass, this, false, waitTime, timeUnit);
    }
}
