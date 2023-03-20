package ru.ccooll.rabbitclient;

import org.jetbrains.annotations.NotNull;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface Client {

    void openConnection() throws IOException, TimeoutException;

    @NotNull AdaptedChannel createChannel();

    void closeConnection() throws IOException;
}
