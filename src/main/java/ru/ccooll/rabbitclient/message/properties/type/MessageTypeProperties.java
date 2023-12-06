package ru.ccooll.rabbitclient.message.properties.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface MessageTypeProperties {

    MessageTypeProperties TEXT_PLAIN = new TextPlainTypeProperties(DeliveryMode.NOT_PERSIST);
    MessageTypeProperties TEXT_PLAIN_PERSIST = new TextPlainTypeProperties(DeliveryMode.PERSIST);
    MessageTypeProperties BINARY = new BinaryTypeProperties(DeliveryMode.NOT_PERSIST);
    MessageTypeProperties BINARY_PERSIST = new BinaryTypeProperties(DeliveryMode.PERSIST);
    MessageTypeProperties JSON = new JsonTypeProperties(DeliveryMode.NOT_PERSIST);
    MessageTypeProperties JSON_PERSIST = new JsonTypeProperties(DeliveryMode.PERSIST);

    String contentType();

    DeliveryMode deliveryMode();

    record TextPlainTypeProperties(DeliveryMode deliveryMode) implements MessageTypeProperties {
        @Override
        public String contentType() {
            return "text/plain";
        }
    }

    record BinaryTypeProperties(DeliveryMode deliveryMode) implements MessageTypeProperties {
        @Override
        public String contentType() {
            return "application/octet-stream";
        }
    }

    record JsonTypeProperties(DeliveryMode deliveryMode) implements MessageTypeProperties {
        @Override
        public String contentType() {
            return "application/json";
        }
    }

    @AllArgsConstructor
    @Getter
    enum DeliveryMode {
        NOT_PERSIST(1),
        PERSIST(2);

        private final int num;
    }
}
