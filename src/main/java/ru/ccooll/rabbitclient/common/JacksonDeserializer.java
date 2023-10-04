package ru.ccooll.rabbitclient.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
public class JacksonDeserializer implements Deserializer {

    private final ObjectMapper objectMapper;

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> targetClass) throws IOException {
        return objectMapper.readValue(bytes, targetClass);
    }
}
