package ru.ccooll.rabbitclient.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface Serializer {

    byte @NotNull [] serialize(@Nullable Object object);
}
