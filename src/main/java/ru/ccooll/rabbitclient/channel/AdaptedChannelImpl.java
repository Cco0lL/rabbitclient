package ru.ccooll.rabbitclient.channel;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import ru.ccooll.rabbitclient.RoutingData;
import ru.ccooll.rabbitclient.common.Deserializer;
import ru.ccooll.rabbitclient.common.Serializer;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingBatchMessages;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingBatchMessagesImpl;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingMessage;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingMessageImpl;
import ru.ccooll.rabbitclient.util.MessagePropertiesUtils;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

public record AdaptedChannelImpl(Channel channel, Serializer serializer,
                                 Deserializer deserializer) implements AdaptedChannel {

    @SneakyThrows
    @Override
    public @NotNull OutgoingMessage send(@NotNull RoutingData data, Object message) {
        val asBytes = serializer.serialize(message);
        val properties = MessagePropertiesUtils.createProperties(UUID.randomUUID());
        return send(data, asBytes, properties);
    }

    @SneakyThrows
    @Override
    public @NotNull OutgoingMessage send(@NotNull RoutingData data, byte @NotNull [] body,
                                         AMQP.@NotNull BasicProperties properties) {
        channel.basicPublish(data.exchange(), data.routingKey(), false, false,
                properties, body);
        return new OutgoingMessageImpl(this, properties, body);
    }

    @Override
    public @NotNull OutgoingBatchMessages sendBatch(@NotNull RoutingData data,
                                                    @NotNull List<Object> messages) {
        val replyToUuid = UUID.randomUUID();
        return new OutgoingBatchMessagesImpl(
                MessagePropertiesUtils.generateReplyToKey(replyToUuid), this,
                messages.stream().map(it -> {
                    val asBytes = serializer.serialize(it);
                    val properties = MessagePropertiesUtils.createBatchedProperties(replyToUuid);
                    return send(data, asBytes, properties);
                }).toList());
    }

    @SneakyThrows
    @Override
    public @NotNull String addConsumer(@NotNull String routingKey, @NotNull DeliverCallback callback) {
        return channel.basicConsume(routingKey, callback, consumerTag -> {});
    }

    @SneakyThrows
    @Override
    public @NotNull String batchConsumer(@NotNull String routingKey, @NotNull BiConsumer<String, Delivery> consumer) {
        return channel.basicConsume(routingKey, false, (consumerTag, message) -> {
            consumer.accept(routingKey, message);
            channel.basicAck(message.getEnvelope().getDeliveryTag(), true);
        }, consumerTag -> {});
    }

    @SneakyThrows
    @Override
    public void removeConsumer(@NotNull String consumerTag) {
        channel.basicCancel(consumerTag);
    }

    @SneakyThrows
    @Override
    public void declareQueue(@NotNull String queue) {
        channel.queueDeclare(queue, false, false, true, null);
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
