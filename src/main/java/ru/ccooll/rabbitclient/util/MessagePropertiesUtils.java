package ru.ccooll.rabbitclient.util;

import com.rabbitmq.client.AMQP;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.UUID;

@UtilityClass
public class MessagePropertiesUtils {

    public static final String END_BATCH_POINTER = "batch-end-pointer";

    public String generateReplyToKey(UUID uuid) {
        return generateReplyToKey(uuid.toString());
    }

    public String generateReplyToKey(String replyTo) {
        return "reply-to." + replyTo;
    }
    
    public AMQP.BasicProperties createWithReplyTo(String replyTo) {
        return create(UUID.randomUUID().toString(), replyTo);
    }

    public AMQP.BasicProperties createWithCorrelationId(String correlationUuid) {
        return create(correlationUuid, UUID.randomUUID().toString());
    }

    public AMQP.BasicProperties create() {
        return create(UUID.randomUUID());
    }

    public AMQP.BasicProperties create(UUID uuid) {
        val asString = uuid.toString();
        return create(asString, asString);
    }
    
    public AMQP.BasicProperties create(String correlationId, String replyTo) {
        return new AMQP.BasicProperties.Builder()
                .correlationId(correlationId)
                .replyTo(generateReplyToKey(replyTo))
                .build();
    }
}
