package ru.ccooll.rabbitclient.message;

import ru.ccooll.rabbitclient.channel.AdaptedChannel;
import ru.ccooll.rabbitclient.message.incoming.Incoming;
import ru.ccooll.rabbitclient.message.incoming.IncomingMessage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@FunctionalInterface
public interface ReceiveConsumer<R, T extends Incoming<R>> {

    void onReceive(CompletableFuture<T> forComplete, IncomingMessage<R> incomingMessage);

    default T receiveMessage(String routingKey, AdaptedChannel channel, Class<R> rClass,
                             boolean autoAck, long waitTime, TimeUnit timeUnit) {
        ReceiveData<T> receiveData = receive(routingKey, channel, rClass, autoAck);
        CompletableFuture<T> future = receiveData.future();
        try {
            if (waitTime == -1) {
                return future.get();
            } else {
                return future.get(waitTime, timeUnit);
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return null;
        } finally {
            channel.removeConsumer(receiveData.consumerTag);
        }
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
