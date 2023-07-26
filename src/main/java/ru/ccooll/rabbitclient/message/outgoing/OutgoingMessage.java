package ru.ccooll.rabbitclient.message.outgoing;

import com.rabbitmq.client.AMQP;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class OutgoingMessage implements Outgoing {

    AdaptedChannel channel;
    AMQP.BasicProperties properties;
    @NonFinal @Getter(AccessLevel.NONE) boolean isRequestedResponse = false;

    @Override
    public void markRequestedResponse() {
        isRequestedResponse = true;
    }

    @Override
    public boolean isRequestedResponse() {
        return isRequestedResponse;
    }
}