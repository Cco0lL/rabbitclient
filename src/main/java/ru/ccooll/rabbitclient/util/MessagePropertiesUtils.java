package ru.ccooll.rabbitclient.util;

import com.rabbitmq.client.AMQP;
import lombok.experimental.UtilityClass;
import java.util.UUID;

@UtilityClass
public class MessagePropertiesUtils {

    public String generateReplyToKey(UUID uuid) {
        return "reply-to." + uuid;
    }
    
    public AMQP.BasicProperties createBatchedProperties(UUID replyToUuuid) {
        return createProperties(UUID.randomUUID(), replyToUuuid);
    }

    public AMQP.BasicProperties createProperties(UUID uuid) {
        return createProperties(uuid, uuid);
    }
    
    public AMQP.BasicProperties createProperties(UUID correlationUuid, UUID replyToUuuid) {
        return new AMQP.BasicProperties.Builder()
                .correlationId(correlationUuid.toString())
                .replyTo(generateReplyToKey(replyToUuuid))
                .build();
    }
}
