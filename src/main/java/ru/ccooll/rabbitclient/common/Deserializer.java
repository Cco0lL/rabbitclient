package ru.ccooll.rabbitclient.common;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Deserializer {

    <T> T deserialize(byte @NotNull [] bytes, @NotNull Class<T> targetClass);
}
