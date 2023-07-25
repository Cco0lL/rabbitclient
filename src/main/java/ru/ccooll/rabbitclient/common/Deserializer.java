package ru.ccooll.rabbitclient.common;

import java.io.IOException;

@FunctionalInterface
public interface Deserializer {

    <T> T deserialize(byte [] bytes, Class<T> targetClass) throws IOException;
}
