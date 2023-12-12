package ru.ccooll.rabbitclient.channel;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Delivery;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import ru.ccooll.rabbitclient.common.Converter;
import ru.ccooll.rabbitclient.error.ErrorHandler;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingBatchMessage;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingMessage;
import ru.ccooll.rabbitclient.util.RoutingData;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public record AdaptedChannelImpl(Channel channel, Converter converter,
                                 ErrorHandler errorHandler) implements AdaptedChannel {

    @Override
    public void declareExchange(String exchangeKey, BuiltinExchangeType type) {
        errorHandler.computeSafe(() -> channel.exchangeDeclare(exchangeKey, type));
    }

    @Override
    public void declareExchange(String exchangeKey, BuiltinExchangeType type,
                                boolean durable, boolean autoDelete, boolean internal,
                                Map<String, Object> arguments) {
        errorHandler.computeSafe(() -> channel.exchangeDeclare(exchangeKey, type,
                durable, autoDelete, internal, arguments));
    }

    @Override
    public void declareQueue(String queueKey, boolean autoDelete) {
        declareQueue(queueKey, false, false, autoDelete, null);
    }

    @Override
    public void declareQueue(String queueKey, boolean durable, boolean autoDelete) {
        declareQueue(queueKey, durable, false, autoDelete, null);
    }

    @Override
    public void declareQueue(String queueKey, boolean durable, boolean exclusive,
                             boolean autoDelete, Map<String, Object> arguments) {
        errorHandler.computeSafe(() -> channel.queueDeclare(queueKey, durable,
                exclusive, autoDelete, arguments));
    }

    @Override
    public void removeExchange(String exchangeKey) {
        errorHandler.computeSafe(() -> channel.exchangeDelete(exchangeKey));
    }

    @Override
    public void removeQueue(String queueKey) {
        errorHandler.computeSafe(() -> channel.queueDelete(queueKey));
    }

    @Override
    public void purgeQueue(String queue) {
        errorHandler.computeSafe(() -> channel.queuePurge(queue));
    }

    @Override
    public void bindQueueToExchange(String queueKey, String exchangeKey, String bindingKey) {
        errorHandler.computeSafe(() -> channel.queueBind(queueKey, exchangeKey, bindingKey));
    }

    @Override
    public void unbindQueueToExchange(String queueKey, String exchangeKey, String bindingKey) {
        errorHandler.computeSafe(() -> channel.queueUnbind(queueKey, exchangeKey, bindingKey));
    }

    @Override
    public boolean isQueueExist(String routingKey) {
        try {
            channel.queueDeclarePassive(routingKey);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    public OutgoingMessage send(RoutingData routingData, OutgoingMessage message) {
        return errorHandler.computeSafe(() -> {
            channel.basicPublish(routingData.exchange(), routingData.routingKey(), message.properties(), message.payload());
            message.setSenderChannel(this);
            return message;
        });
    }

    @Override
    public OutgoingMessage convertAndSend(RoutingData routingData, Object message, boolean persists) {
        return send(routingData, converter.convert(message, persists));
    }

    @Override
    public OutgoingBatchMessage send(RoutingData routingData, OutgoingBatchMessage outgoingBatchMessage) {
        val iterator = outgoingBatchMessage.payloadList().iterator();
        val properties = outgoingBatchMessage.properties();
        while (iterator.hasNext()) {
            val frame = iterator.next();
            if (!iterator.hasNext()) {
                send(routingData, new OutgoingMessage(frame, outgoingBatchMessage.lastMessageProperties()));
                break;
            }
            send(routingData, new OutgoingMessage(frame, properties));
        }
        outgoingBatchMessage.setSenderChannel(this);
        return outgoingBatchMessage;
    }

    @Override
    public OutgoingBatchMessage convertAndSend(RoutingData routingData, List<Object> messages, boolean persists) {
        return send(routingData, converter().convert(messages, persists));
    }

    @Override
    public @Nullable String addConsumer(String routingKey, boolean autoAck, BiConsumer<String, Delivery> consumer) {
        return errorHandler.computeSafe(() ->
                channel.basicConsume(routingKey, autoAck, consumer::accept,
                        (consumerTag, sig) -> { /*TODO*/ }));
    }

    @Override
    public void removeConsumer(String consumerTag) {
        errorHandler.computeSafe(() -> channel.basicCancel(consumerTag));
    }

    @Override
    public void ack(long deliveryTag, boolean multiple) {
        errorHandler.computeSafe(() -> channel.basicAck(deliveryTag, multiple));
    }

    @Override
    public void close() {
        errorHandler.computeSafe(() -> channel.close());
    }
}
