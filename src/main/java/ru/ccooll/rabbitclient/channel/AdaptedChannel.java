package ru.ccooll.rabbitclient.channel;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ccooll.rabbitclient.RoutingData;
import ru.ccooll.rabbitclient.common.Deserializer;
import ru.ccooll.rabbitclient.common.Serializer;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingBatchMessages;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingMessage;

import java.util.List;
import java.util.function.BiConsumer;

public interface AdaptedChannel {

    @NotNull OutgoingMessage send(@NotNull RoutingData data, @Nullable Object message);

    @NotNull OutgoingMessage send(@NotNull RoutingData data, byte @NotNull [] body,
                                  @NotNull AMQP.BasicProperties properties);

    @NotNull OutgoingBatchMessages sendBatch(@NotNull RoutingData data,
                                             @NotNull List<@Nullable Object> messages);

    @NotNull String addConsumer(@NotNull String routingKey, @NotNull DeliverCallback callback);

    @NotNull String batchConsumer(@NotNull String routingKey, @NotNull BiConsumer<String, Delivery> callback);

    void removeConsumer(@NotNull String consumerTag);

    void declareQueue(@NotNull String queue);

    @NotNull Deserializer deserializer();

    @NotNull Serializer serializer();

    boolean isQueueExist(@NotNull String routingKey);

    void close();
}
