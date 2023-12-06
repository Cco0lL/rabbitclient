package ru.ccooll.rabbitclient.message.properties.reply;

public class ReplyToConstantNameStrategy implements ReplyToNameStrategy {

    private final String name;

    public ReplyToConstantNameStrategy(ReplyToNameStrategy strategy) {
        this.name = strategy.create();
    }

    @Override
    public String create() {
        return name;
    }
}
