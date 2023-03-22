package ru.ccooll.rabbitclient.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SimpleSerializer implements Serializer {

    @Override
    public byte @NotNull [] serialize(@Nullable Object object) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
             ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeObject(object);
            return baos.toByteArray();
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
