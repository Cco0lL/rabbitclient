package ru.ccooll.rabbitclient.message.outgoing;

import com.rabbitmq.client.AMQP;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;
import ru.ccooll.rabbitclient.message.ReceiveConsumer;
import ru.ccooll.rabbitclient.message.incoming.Incoming;
import ru.ccooll.rabbitclient.message.incoming.IncomingMessage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@FunctionalInterface
interface ResponseConsumer<R, T extends Incoming<R>> {

    void consume(Class<R> rclass, IncomingMessage<R> message,
                 CompletableFuture<T> forComplete);

    default T consumeResponse(Class<R> rClass, Outgoing message,
                                                 boolean autoAck, long waitTime, TimeUnit timeUnit) {
        if (message.isRequestedResponse()) {
            throw new IllegalStateException("response already requested");
        }
        AdaptedChannel channel = message.channel();
        if (channel == null) {
            throw new IllegalStateException("Message wasn't sent");
        }

        AMQP.BasicProperties properties = message.properties();
        String replyTo = properties.getReplyTo();
        channel.declareQueue(replyTo, true);

        ReceiveConsumer<R, T> receiveConsumer = (forComplete, incomingMessage) ->
                consume(rClass, incomingMessage, forComplete);

        T response = receiveConsumer.receiveMessage(replyTo, channel, rClass, autoAck, waitTime, timeUnit);
        channel.removeQueue(replyTo);
        return response;
    }
}
