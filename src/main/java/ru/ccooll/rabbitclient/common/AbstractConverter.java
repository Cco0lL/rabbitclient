package ru.ccooll.rabbitclient.common;

import lombok.AllArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
public abstract class AbstractConverter<S extends Serializer,
        D extends Deserializer> implements Converter {

    protected S serializer;
    protected D deserializer;

    @Override
    public byte[] convertToBytes(Object object) throws IOException {
        return serializer.serialize(object);
    }

    @Override
    public <T> T convertFromBytes(byte[] bytes, Class<T> targetClass) throws IOException {
        return deserializer.deserialize(bytes, targetClass);
    }
}
