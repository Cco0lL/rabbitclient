package ru.ccooll.rabbitclient.message.incoming;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.Envelope;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class IncomingBatchMessage<T> implements Incoming<T> {

    AdaptedChannel channel;
    Envelope envelope;
    AMQP.BasicProperties properties;
    List<T> message;
    @NonFinal @Getter(AccessLevel.NONE) boolean isAlreadyResponded = false;

    @Override
    public Envelope envelope() {
        return envelope;
    }

    @Override
    public void markAsResponded() {
        isAlreadyResponded = true;
    }

    @Override
    public boolean isAlreadyResponded() {
        return isAlreadyResponded;
    }
}
