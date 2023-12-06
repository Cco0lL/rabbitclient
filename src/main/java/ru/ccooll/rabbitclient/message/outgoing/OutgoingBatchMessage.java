package ru.ccooll.rabbitclient.message.outgoing;

import com.rabbitmq.client.AMQP;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ru.ccooll.rabbitclient.channel.AdaptedChannel;
import ru.ccooll.rabbitclient.message.incoming.IncomingBatchMessage;
import ru.ccooll.rabbitclient.message.incoming.IncomingMessage;

import java.util.concurrent.CompletableFuture;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class OutgoingBatchMessage implements Outgoing {

    public static final OutgoingBatchMessage EMPTY = new OutgoingBatchMessage(null, null) {
        @Override
        public boolean isRequestedResponse() {
            return false;
        }

        @Override
        public void markRequestedResponse() {
            throw new UnsupportedOperationException("empty batch can't request any message");
        }

        @Override
        public <T> CompletableFuture<IncomingMessage<T>> responseRequest(Class<T> rClass) {
            throw new UnsupportedOperationException("empty batch can't request any message");
        }

        @Override
        public <T> CompletableFuture<IncomingBatchMessage<T>> responseRequestBatch(Class<T> rClass) {
            throw new UnsupportedOperationException("empty batch can't request any message");
        }
    };

    AdaptedChannel channel;
    AMQP.BasicProperties properties;
    @NonFinal @Getter(AccessLevel.NONE) boolean isRequestedResponse = false;

    @Override
    public void markRequestedResponse() {
        isRequestedResponse = true;
    }

    @Override
    public boolean isRequestedResponse() {
        return isRequestedResponse;
    }
}
