package ru.ccooll.rabbitclient.message.incoming;

import lombok.val;
import ru.ccooll.rabbitclient.message.Message;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingBatchMessage;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingMessage;
import ru.ccooll.rabbitclient.message.properties.MutableMessageProperties;
import ru.ccooll.rabbitclient.message.properties.reply.ReplyToConstantNameStrategy;
import ru.ccooll.rabbitclient.util.RoutingData;

import java.util.List;

/**
 * represents incoming message interface that able to
 * send response to sender
 *
 * @param <T> - message object type
 */
public interface Incoming<T> extends Message {

    T message();

    void markAsResponded();

    boolean isAlreadyResponded();

    default <R> OutgoingMessage sendResponse(R response, MutableMessageProperties responseProperties) {
        if (isAlreadyResponded()) {
            throw new IllegalStateException("Already responded");
        }

        val properties = properties();
        val channel = channel();
        val routingData = RoutingData.of(properties.getReplyTo());

        val outgoing = channel.convertAndSend(routingData, response, responseProperties
                .correlationId(properties.getCorrelationId()));
        markAsResponded();
        return outgoing;
    }

    default <R> OutgoingBatchMessage sendResponseBatch(List<R> response, MutableMessageProperties responseProperties) {
        if (isAlreadyResponded()) {
            throw new IllegalStateException("Already responded");
        }

        val properties = properties();
        val channel = channel();
        val routingData = RoutingData.of(properties.getReplyTo());

        //noinspection unchecked
        val outgoing = channel.convertAndSend(routingData, (List<Object>) response,
                responseProperties
                        .replyToNameStrategy(new ReplyToConstantNameStrategy(responseProperties.replyToNameStrategy()))
                        .correlationId(properties.getCorrelationId()));

        markAsResponded();
        return outgoing;
    }
}
