package ru.ccooll.rabbitclient.common.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import ru.ccooll.rabbitclient.common.Deserializer;

import java.io.IOException;

@AllArgsConstructor
public class JacksonDeserializer implements Deserializer {

    private ObjectMapper objectMapper;

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> targetClass) throws IOException {
        return objectMapper.readValue(bytes, targetClass);
    }

    void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
