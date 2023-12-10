package ru.ccooll.rabbitclient.common;

import lombok.val;
import ru.ccooll.rabbitclient.error.ErrorHandler;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingBatchMessage;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingMessage;
import ru.ccooll.rabbitclient.message.properties.MutableMessageProperties;

import java.util.ArrayList;
import java.util.List;

public interface Converter {

    Deserializer deserializer();

    Serializer serializer();

    ErrorHandler errorHandler();

    void errorHandler(ErrorHandler errorHandler);

    MutableMessageProperties newDefaultProperties(boolean persists);

    default OutgoingMessage convert(Object message, boolean persists) {
        val properties = newDefaultProperties(persists);
        return convert(message, properties);
    }

    default OutgoingMessage convert(Object message, MutableMessageProperties properties) {
        val serializer = serializer();
        val payload = errorHandler().computeSafe(() -> serializer.serialize(message));
        return new OutgoingMessage(payload, properties.toImmutableProperties());
    }

    default OutgoingBatchMessage convert(List<Object> messages, boolean persists) {
        val properties = newDefaultProperties(persists);
        return convert(messages, properties);
    }

    default OutgoingBatchMessage convert(List<Object> messages, MutableMessageProperties properties) {
        if (messages.isEmpty()) {
            return OutgoingBatchMessage.empty();
        }
        val serializer = serializer();
        val errorHandler = errorHandler();
        val payload = new ArrayList<byte[]>();
        messages.forEach(mes -> payload.add(errorHandler.computeSafe(() -> serializer.serialize(mes))));
        return new OutgoingBatchMessage(payload, properties);
    }

    default <T> T convert(byte[] bytes, Class<T> tClass) {
        return errorHandler().computeSafe(() -> deserializer().deserialize(bytes, tClass));
    }
}
