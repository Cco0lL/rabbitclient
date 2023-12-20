package ru.ccooll.rabbitclient.message.outgoing;

import com.rabbitmq.client.AMQP;
import lombok.Getter;
import lombok.experimental.Accessors;
import ru.ccooll.rabbitclient.message.incoming.IncomingBatchMessage;
import ru.ccooll.rabbitclient.message.incoming.IncomingMessage;
import ru.ccooll.rabbitclient.message.properties.MutableMessageProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Getter
@Accessors(fluent = true)
public class OutgoingBatchMessage extends AbstractOutgoing {

    public static final String END_BATCH_POINTER = "batch-end-pointer";
    private static final OutgoingBatchMessage EMPTY = new OutgoingBatchMessage(new ArrayList<>(),
            new MutableMessageProperties()) {
        @Override
        public boolean isRequestedResponse() {
            return false;
        }

        @Override
        public void markRequestedResponse() {
            throw new UnsupportedOperationException("empty batch can't request any message");
        }

        @Override
        public <T> IncomingMessage<T> responseRequest(Class<T> rClass) {
            throw new UnsupportedOperationException("empty batch can't request any message");
        }

        @Override
        public <T> IncomingMessage<T> responseRequest(Class<T> rClass, long waitTime, TimeUnit timeUnit) {
            throw new UnsupportedOperationException("empty batch can't request any message");
        }

        @Override
        public <T> IncomingBatchMessage<T> responseRequestBatch(Class<T> rClass) {
            throw new UnsupportedOperationException("empty batch can't request any message");
        }

        @Override
        public <T> IncomingBatchMessage<T> responseRequestBatch(Class<T> rClass, long waitTime, TimeUnit timeUnit) {
            throw new UnsupportedOperationException("empty batch can't request any message");
        }
    };

    private final List<byte[]> payloadList;
    private final AMQP.BasicProperties lastMessageProperties;

    public OutgoingBatchMessage(List<byte[]> payloadList, MutableMessageProperties properties) {
        super(properties.toImmutableProperties());
        this.payloadList = payloadList;
        properties.headers().put(END_BATCH_POINTER, true);
        lastMessageProperties = properties.toImmutableProperties();
    }

    public static OutgoingBatchMessage empty() {
        return EMPTY;
    }
}
