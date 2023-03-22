package ru.ccooll.rabbitclient.channel;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ccooll.rabbitclient.RoutingData;
import ru.ccooll.rabbitclient.common.Deserializer;
import ru.ccooll.rabbitclient.common.Serializer;
import ru.ccooll.rabbitclient.message.incoming.IncomingMessage;
import ru.ccooll.rabbitclient.message.incoming.IncomingMessageImpl;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingBatchMessages;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingBatchMessagesImpl;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingMessage;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingMessageImpl;
import ru.ccooll.rabbitclient.util.MessagePropertiesUtils;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public record AdaptedChannelImpl(Channel channel, Serializer serializer,
                                 Deserializer deserializer) implements AdaptedChannel {

    @SneakyThrows
    @Override
    public @NotNull OutgoingMessage send(@NotNull RoutingData data, @NotNull Object message) {
        val properties = MessagePropertiesUtils.create(UUID.randomUUID());
        return send(data, message, properties);
    }

    @Override
    public @NotNull OutgoingMessage send(@NotNull RoutingData data, @Nullable Object message,
                                         @NotNull AMQP.BasicProperties properties) {
        val asBytes = serializer.serialize(message);
        return send(data, asBytes, properties);
    }

    @SneakyThrows
    @Override
    public @NotNull OutgoingMessage send(@NotNull RoutingData data, byte @NotNull [] body,
                                         @NotNull AMQP.BasicProperties properties) {
        channel.basicPublish(data.exchange(), data.routingKey(), false, false, properties, body);
        return new OutgoingMessageImpl(this, properties, body);
    }

    @Override
    public @NotNull OutgoingBatchMessages sendBatch(@NotNull RoutingData data,
                                                    @NotNull List<Object> messages) {
        val replyToUuid = UUID.randomUUID().toString();
        return new OutgoingBatchMessagesImpl(
                MessagePropertiesUtils.generateReplyToKey(replyToUuid), this,
                messages.stream().map(it -> {
                    val properties = MessagePropertiesUtils.createWithReplyTo(replyToUuid);
                    return send(data, it, properties);
                }).toList());
    }

    @Override
    public @NotNull String addConsumer(@NotNull String routingKey, @NotNull DeliverCallback callback) {
        return addConsumer(routingKey, true, callback);
    }

    @SneakyThrows
    @Override
    public @NotNull String addConsumer(@NotNull String routingKey, boolean autoAck,
                                       @NotNull DeliverCallback callback) {
        return channel.basicConsume(routingKey, autoAck, callback, consumerTag -> {});
    }


    @SneakyThrows
    @Override
    public void removeConsumer(@NotNull String consumerTag) {
        channel.basicCancel(consumerTag);
    }

    @SneakyThrows
    @Override
    public void removeBatchConsumer(@NotNull String consumerTag, long lastDeliveryTag) {
        channel.basicAck(lastDeliveryTag, true);
        removeConsumer(consumerTag);
    }

    @SneakyThrows
    @Override
    public void declareQueue(@NotNull String queue) {
        channel.queueDeclare(queue, false, false, true, null);
    }

    @Override
    public @NotNull <T> IncomingMessage<T> deliveryToIncomeMessage(@NotNull Delivery delivery, @NotNull Class<T> iClass) {
        val message = deserializer.deserialize(delivery.getBody(), iClass);
        return new IncomingMessageImpl<>(this, delivery.getProperties(), message);
    }

    @Override
    public boolean isQueueExist(@NotNull String routingKey) {
        try {
            channel.queueDeclarePassive(routingKey);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @SneakyThrows
    @Override
    public void close() {
        channel.close();
    }
}
