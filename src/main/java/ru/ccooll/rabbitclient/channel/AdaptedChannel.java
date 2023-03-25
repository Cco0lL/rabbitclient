package ru.ccooll.rabbitclient.channel;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import org.jetbrains.annotations.NotNull;
import ru.ccooll.rabbitclient.RoutingData;
import ru.ccooll.rabbitclient.common.Deserializer;
import ru.ccooll.rabbitclient.common.Serializer;
import ru.ccooll.rabbitclient.message.incoming.IncomingMessage;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingBatchMessages;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingMessage;

import java.util.List;

public interface AdaptedChannel {

    @NotNull OutgoingMessage send(@NotNull RoutingData data, @NotNull Object message);

    @NotNull OutgoingMessage send(@NotNull RoutingData data, @NotNull Object message,
                                  @NotNull AMQP.BasicProperties properties);

    @NotNull OutgoingMessage send(@NotNull RoutingData data, byte[] body,
                                  @NotNull AMQP.BasicProperties properties);

    @NotNull OutgoingBatchMessages sendBatch(@NotNull RoutingData data,
                                             @NotNull List<@NotNull Object> messages);

    @NotNull String addConsumer(@NotNull String routingKey, @NotNull DeliverCallback callback);

    @NotNull String addConsumer(@NotNull String routingKey, boolean autoAck,
                                @NotNull DeliverCallback callback);

    void removeConsumer(@NotNull String consumerTag);

    void removeBatchConsumer(@NotNull String consumerTag, long lastDeliveryTag);

    void declareQueue(@NotNull String queue);

    @NotNull <T> IncomingMessage<T> deliveryToIncomeMessage(@NotNull Delivery delivery,
                                                            @NotNull Class<T> iClass);

    @NotNull Deserializer deserializer();

    @NotNull Serializer serializer();

    boolean isQueueExist(@NotNull String routingKey);

    void close();
}
