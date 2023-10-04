package ru.ccooll.rabbitclient.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
public class JacksonSerializer implements Serializer {

    private final ObjectMapper jsonMapper;

    @Override
    public byte[] serialize(Object o) throws IOException {
        return jsonMapper.writeValueAsBytes(o);
    }
}