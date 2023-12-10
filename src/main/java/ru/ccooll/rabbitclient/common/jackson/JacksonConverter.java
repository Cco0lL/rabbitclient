package ru.ccooll.rabbitclient.common.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import ru.ccooll.rabbitclient.common.AbstractConverter;
import ru.ccooll.rabbitclient.message.properties.MutableMessageProperties;
import ru.ccooll.rabbitclient.message.properties.type.MessageTypeProperties;

@Getter
public class JacksonConverter extends AbstractConverter<JacksonSerializer, JacksonDeserializer> {

    private ObjectMapper objectMapper;

    public JacksonConverter(ObjectMapper objectMapper) {
        super(new JacksonSerializer(objectMapper), new JacksonDeserializer(objectMapper));
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        serializer.setObjectMapper(objectMapper);
        deserializer.setObjectMapper(objectMapper);
    }

    @Override
    public MutableMessageProperties newDefaultProperties(boolean persists) {
        return new MutableMessageProperties().messageTypeProperties(persists
                ? MessageTypeProperties.JSON_PERSIST
                : MessageTypeProperties.JSON);
    }
}
