package ru.ccooll.rabbitclient.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class SimpleDeserializer implements Deserializer {

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> targetClass) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream in = new ObjectInputStream(bais)) {
            //noinspection unchecked
            return (T) in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("class od deserialized objects not found");
        }
    }
}
