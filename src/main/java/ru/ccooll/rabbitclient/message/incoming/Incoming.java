package ru.ccooll.rabbitclient.message.incoming;

import com.rabbitmq.client.AMQP;
import lombok.val;
import ru.ccooll.rabbitclient.util.RoutingData;
import ru.ccooll.rabbitclient.message.Message;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingBatchMessage;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingMessage;
import ru.ccooll.rabbitclient.util.MessagePropertiesUtils;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * represents incoming message interface that able to
 * send response to sender
 * @param <T> - message object type
 */
public interface Incoming<T> extends Message {

    T message();

    void markAsResponded();

    boolean isAlreadyResponded();

    default <R> OutgoingMessage sendResponse(R response) {
        if (isAlreadyResponded()) {
            throw new IllegalStateException("Already responded");
        }

        val properties = properties();
        val channel = channel();
        val routingData = RoutingData.of(properties.getReplyTo());
        val responseProperties = MessagePropertiesUtils.createWithCorrelationId(properties.getCorrelationId());

        val outgoing = channel.convertAndSend(routingData, response, responseProperties);
        markAsResponded();
        return outgoing;
    }

    default <R> OutgoingBatchMessage sendResponseBatch(List<R> response) {
        if (isAlreadyResponded()) {
            throw new IllegalStateException("Already responded");
        }

        val properties = properties();
        val channel = channel();
        val routingData = RoutingData.of(properties.getReplyTo());

        val headers = new HashMap<String, Object>();
        headers.put(MessagePropertiesUtils.END_BATCH_POINTER, true);

        val responseProperties = MessagePropertiesUtils.create(properties.getCorrelationId(),
                MessagePropertiesUtils.generateReplyToKey(), headers);

        //noinspection unchecked
        val outgoing = channel.convertAndSend(routingData, (List<Object>) response, responseProperties);
        markAsResponded();
        return outgoing;
    }
}
