package ru.ccooll.rabbitclient.message.properties.reply;

import java.util.UUID;

public class SimpleReplyToStrategy implements ReplyToNameFromUUIDStrategy {

    private static final SimpleReplyToStrategy INSTANCE = new SimpleReplyToStrategy();

    private SimpleReplyToStrategy() {}

    public static SimpleReplyToStrategy get() {
        return INSTANCE;
    }

    @Override
    public String fromUuid(UUID uuid) {
        return "reply-to." + uuid;
    }
}
