package ru.ccooll.rabbitclient.common.simple;

import ru.ccooll.rabbitclient.common.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SimpleSerializer implements Serializer {

    @Override
    public byte [] serialize(Object object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(object);
        return baos.toByteArray();
    }
}
