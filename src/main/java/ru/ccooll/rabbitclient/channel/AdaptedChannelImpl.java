package ru.ccooll.rabbitclient.channel;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Delivery;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import ru.ccooll.rabbitclient.common.Deserializer;
import ru.ccooll.rabbitclient.common.Serializer;
import ru.ccooll.rabbitclient.error.ErrorHandler;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingBatchMessage;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingMessage;
import ru.ccooll.rabbitclient.message.properties.MutableMessageProperties;
import ru.ccooll.rabbitclient.util.MessagePropertiesUtils;
import ru.ccooll.rabbitclient.util.RoutingData;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public record AdaptedChannelImpl(Channel channel, Serializer serializer, Deserializer deserializer,
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
        errorHandler.computeSafe(() ->
                channel.queueDeclare(queueKey, false, false, autoDelete, null));
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
    public OutgoingMessage convertAndSend(RoutingData routingData, Object message, MutableMessageProperties properties) {
        return errorHandler.computeSafe(() -> {
            byte[] body = serializer.serialize(message);
            AMQP.BasicProperties immutable = properties.toImmutableProperties();
            channel.basicPublish(routingData.exchange(), routingData.routingKey(), immutable, body);
            return new OutgoingMessage(this, immutable);
        });
    }

    @Override
    public OutgoingBatchMessage convertAndSend(RoutingData routingData, List<Object> messages,
                                               MutableMessageProperties properties) {
        if (messages.isEmpty()) {
            return OutgoingBatchMessage.EMPTY;
        }

        val iterator = messages.iterator();
        AMQP.BasicProperties lastProperties = null;
        while (iterator.hasNext()) {
            val message = iterator.next();
            if (!iterator.hasNext()) {
                properties.headers().put(MessagePropertiesUtils.END_BATCH_POINTER, true);
            }
            lastProperties = convertAndSend(routingData, message, properties).properties();
        }

        return new OutgoingBatchMessage(this, lastProperties);
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
