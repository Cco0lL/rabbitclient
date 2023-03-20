package ru.ccooll.rabbitclient.message;

import com.rabbitmq.client.AMQP;
import org.jetbrains.annotations.NotNull;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;

public interface Message {

    @NotNull AdaptedChannel channel();

    @NotNull AMQP.BasicProperties properties();
}
