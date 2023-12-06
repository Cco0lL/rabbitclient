package ru.ccooll.rabbitclient.message.properties.reply;

import java.util.UUID;

@FunctionalInterface
public interface ReplyToNameFromUUIDStrategy extends ReplyToNameStrategy {

    String fromUuid(UUID uuid);

    @Override
    default String create() {
        return fromUuid(UUID.randomUUID());
    }
}
