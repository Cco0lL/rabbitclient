package ru.ccooll.rabbitclient.common;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class SimpleDeserializer implements Deserializer {

    @Override
    public <T> T deserialize(byte @NotNull [] bytes, @NotNull Class<T> targetClass) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream in = new ObjectInputStream(bais)) {
            //noinspection unchecked
            return (T) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
