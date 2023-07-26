package ru.ccooll.rabbitclient.channel;

import com.rabbitmq.client.*;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import ru.ccooll.rabbitclient.util.RoutingData;
import ru.ccooll.rabbitclient.common.Deserializer;
import ru.ccooll.rabbitclient.common.Serializer;
import ru.ccooll.rabbitclient.error.ErrorHandler;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingBatchMessage;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingMessage;
import ru.ccooll.rabbitclient.util.MessagePropertiesUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

public record AdaptedChannelImpl(Channel channel, Serializer serializer, Deserializer deserializer,
                                 ErrorHandler errorHandler) implements AdaptedChannel {

    @Override
    public void declareExchange(String exchangeKey, BuiltinExchangeType type) {
        errorHandler.computeSafe(() -> channel.exchangeDeclare(exchangeKey, type));
    }

    @Override
    public void declareQueue(String queueKey, boolean autoDelete) {
        errorHandler.computeSafe(() ->
                channel.queueDeclare(queueKey, false, false, autoDelete, null));
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
    public OutgoingMessage send(RoutingData routingData, byte[] body) {
        return errorHandler.computeSafe(() -> {
            val properties = MessagePropertiesUtils.create();
            channel.basicPublish(routingData.exchange(), routingData.routingKey(), properties, body);
            return new OutgoingMessage(this, properties);
        });
    }

    @Override
    public OutgoingMessage convertAndSend(RoutingData routingData, Object message, AMQP.BasicProperties properties) {
        return errorHandler.computeSafe(() -> {
            byte[] body = serializer.serialize(message);
            channel.basicPublish(routingData.exchange(), routingData.routingKey(), properties, body);
            return new OutgoingMessage(this, properties);
        });
    }

    @Override
    public OutgoingMessage prepareAndSend(RoutingData routingData, Object message) {
        return convertAndSend(routingData, message, MessagePropertiesUtils.create(UUID.randomUUID()));
    }

    @Override
    public OutgoingBatchMessage convertAndSend(RoutingData routingData, List<Object> messages,
                                               AMQP.BasicProperties properties) {
        val iterator = messages.iterator();
        while (iterator.hasNext()) {
            val message = iterator.next();
            //last message in batch defines batch properties
            val messageProperties = iterator.hasNext() ? MessagePropertiesUtils.createWithCorrelationId(properties.getCorrelationId())
                    : properties;
            convertAndSend(routingData, message, messageProperties);
        }

        return new OutgoingBatchMessage(this, properties);
    }

    @Override
    public OutgoingBatchMessage prepareAndSend(RoutingData routingData, List<Object> messages) {
        val headers = new HashMap<String, Object>();
        headers.put(MessagePropertiesUtils.END_BATCH_POINTER, true);
        val properties = MessagePropertiesUtils.create(UUID.randomUUID().toString(),
                MessagePropertiesUtils.generateReplyToKey(), headers);
        return convertAndSend(routingData, messages, properties);
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
