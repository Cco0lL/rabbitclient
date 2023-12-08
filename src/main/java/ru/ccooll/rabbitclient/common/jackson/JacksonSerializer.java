package ru.ccooll.rabbitclient.common.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import ru.ccooll.rabbitclient.common.Serializer;

import java.io.IOException;

@AllArgsConstructor
public class JacksonSerializer implements Serializer {

    private ObjectMapper objectMapper;

    @Override
    public byte[] serialize(Object o) throws IOException {
        return objectMapper.writeValueAsBytes(o);
    }

    void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}