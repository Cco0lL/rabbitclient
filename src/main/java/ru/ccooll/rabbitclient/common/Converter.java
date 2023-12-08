package ru.ccooll.rabbitclient.common;

import java.io.IOException;

public interface Converter {

    byte[] convertToBytes(Object object) throws IOException;

    <T> T convertFromBytes(byte [] bytes, Class<T> targetClass) throws IOException;
}
