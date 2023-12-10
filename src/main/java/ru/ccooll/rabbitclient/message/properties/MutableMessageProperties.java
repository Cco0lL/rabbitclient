package ru.ccooll.rabbitclient.message.properties;

import com.rabbitmq.client.AMQP;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.ccooll.rabbitclient.message.properties.reply.ReplyToNameStrategy;
import ru.ccooll.rabbitclient.message.properties.reply.SimpleReplyToStrategy;
import ru.ccooll.rabbitclient.message.properties.type.MessageTypeProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true, fluent = true)
@Getter @Setter
public class MutableMessageProperties {

    private MessageTypeProperties messageTypeProperties;
    private ReplyToNameStrategy replyToNameStrategy = SimpleReplyToStrategy.get();
    private String correlationId = UUID.randomUUID().toString();
    private final Map<String, Object> headers = new HashMap<>();

    public AMQP.BasicProperties toImmutableProperties() {
        return new AMQP.BasicProperties.Builder()
                .contentType(messageTypeProperties.contentType())
                .deliveryMode(messageTypeProperties.deliveryMode().getNum())
                .correlationId(correlationId)
                .replyTo(replyToNameStrategy.create())
                .headers(headers)
                .build();
    }
}
