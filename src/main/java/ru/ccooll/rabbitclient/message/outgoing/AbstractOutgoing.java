package ru.ccooll.rabbitclient.message.outgoing;

import com.rabbitmq.client.AMQP;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public abstract class AbstractOutgoing implements Outgoing {

    protected final AMQP.BasicProperties properties;
    protected AdaptedChannel channel;
    @Getter(AccessLevel.NONE) boolean isRequestedResponse = false;

    @Override
    public void setSenderChannel(AdaptedChannel sender) {
        channel = sender;
    }

    @Override
    public void markRequestedResponse() {
        isRequestedResponse = true;
    }

    @Override
    public boolean isRequestedResponse() {
        return isRequestedResponse;
    }
}
