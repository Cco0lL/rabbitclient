package ru.ccooll.rabbitclient.message.properties.reply;

@FunctionalInterface
public interface ReplyToNameStrategy {

    String create();
}
