package ru.ccooll.rabbitclient.common;

import java.io.IOException;

@FunctionalInterface
public interface Serializer {

    byte[] serialize(Object object) throws IOException;
}
