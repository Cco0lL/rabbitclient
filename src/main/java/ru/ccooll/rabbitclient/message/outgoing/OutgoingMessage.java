package ru.ccooll.rabbitclient.message.outgoing;

import com.rabbitmq.client.AMQP;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class OutgoingMessage extends AbstractOutgoing {

    private final byte[] payload;

    public OutgoingMessage(byte[] payload, AMQP.BasicProperties properties) {
        super(properties);
        this.payload = payload;
    }
}