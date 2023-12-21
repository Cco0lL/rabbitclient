package ru.ccooll.rabbitclient.message;

import ru.ccooll.rabbitclient.channel.AdaptedChannel;
import ru.ccooll.rabbitclient.message.incoming.Incoming;
import ru.ccooll.rabbitclient.message.incoming.IncomingMessage;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface ReceiveConsumer<R, T extends Incoming<R>> {

    void onReceive(CompletableFuture<T> forComplete, IncomingMessage<R> incomingMessage);

    default CompletableFuture<T> receiveMessage(String routingKey, AdaptedChannel channel,
                                                Class<R> rClass, boolean autoAck) {
        ReceiveData<T> receiveData = receive(routingKey, channel, rClass, autoAck);
        return receiveData.future().thenApply(it -> {
            channel.removeConsumer(receiveData.consumerTag);
            return it;
        });
    }

    default ReceiveData<T> receive(String routingKey, AdaptedChannel channel,
                                   Class<R> rClass, boolean autoAck) {
        CompletableFuture<T> future = new CompletableFuture<>();
        String consumerTag = channel.addConsumer(routingKey, autoAck, rClass,
                mes -> onReceive(future, mes));
        return new ReceiveData<>(consumerTag, future);
    }

    record ReceiveData<T>(String consumerTag, CompletableFuture<T> future) {}
}
