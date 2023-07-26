package ru.ccooll.rabbitclient.message.incoming;

import lombok.val;
import ru.ccooll.rabbitclient.util.RoutingData;
import ru.ccooll.rabbitclient.message.Message;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingBatchMessage;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingMessage;
import ru.ccooll.rabbitclient.util.MessagePropertiesUtils;

import java.util.List;

/**
 * represents incoming message interface that able to
 * send response to sender
 * @param <T> - message object type
 */
public interface Incoming<T> extends Message {

    T message();

    /**
     * @return true if sender waits for response
     */
    boolean isWaitingForResponse();

    default <R> OutgoingMessage sendResponse(R response) {
        val properties = properties();
        val channel = channel();
        val routingData = RoutingData.of(properties.getReplyTo());
        val responseProperties = MessagePropertiesUtils.createWithCorrelationId(properties.getCorrelationId());
        return channel.convertAndSend(routingData, response, responseProperties);
    }

    default <R> OutgoingBatchMessage sendResponseBatch(List<R> response) {
        val properties = properties();
        val channel = channel();
        val routingData = RoutingData.of(properties.getReplyTo());
        val responseProperties = MessagePropertiesUtils.createWithCorrelationId(properties.getCorrelationId());
        responseProperties.getHeaders().put(MessagePropertiesUtils.END_BATCH_POINTER, true);
        //noinspection unchecked
        return channel.convertAndSend(routingData, (List<Object>) response, responseProperties);
    }
}
