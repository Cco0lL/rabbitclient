package ru.ccooll.rabbitclient.util;

import com.rabbitmq.client.AMQP;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@UtilityClass
public class MessagePropertiesUtils {

    public static final String END_BATCH_POINTER = "batch-end-pointer";

    public String generateReplyToKey() {
        return generateReplyToKey(UUID.randomUUID());
    }

    public String generateReplyToKey(UUID uuid) {
        return generateReplyToKey(uuid.toString());
    }

    public String generateReplyToKey(String replyTo) {
        return "reply-to." + replyTo;
    }
    
    public AMQP.BasicProperties createWithReplyTo(String replyTo) {
        return create(UUID.randomUUID().toString(), replyTo, null);
    }

    public AMQP.BasicProperties createWithCorrelationId(String correlationUuid) {
        return create(correlationUuid, UUID.randomUUID().toString(), null);
    }

    public AMQP.BasicProperties create() {
        return create(UUID.randomUUID());
    }

    public AMQP.BasicProperties create(UUID uuid) {
        val asString = uuid.toString();
        return create(asString, asString, null);
    }
    
    public AMQP.BasicProperties create(String correlationId, String replyTo,
                                       Map<String, Object> headers) {
        return new AMQP.BasicProperties.Builder()
                .correlationId(correlationId)
                .replyTo(generateReplyToKey(replyTo))
                .headers(headers)
                .build();
    }
}
