package ru.ccooll.rabbitclient.message;

import org.jetbrains.annotations.NotNull;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;

public interface BatchMessages {

    @NotNull String replyToKey();

    @NotNull AdaptedChannel channel();
}
