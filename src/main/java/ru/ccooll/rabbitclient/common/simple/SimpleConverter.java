package ru.ccooll.rabbitclient.common.simple;

import ru.ccooll.rabbitclient.common.AbstractConverter;
import ru.ccooll.rabbitclient.message.properties.MutableMessageProperties;
import ru.ccooll.rabbitclient.message.properties.type.MessageTypeProperties;

public class SimpleConverter extends AbstractConverter<SimpleSerializer, SimpleDeserializer> {

    public SimpleConverter() {
        super(new SimpleSerializer(), new SimpleDeserializer());
    }

    @Override
    public MutableMessageProperties newDefaultProperties(boolean persists) {
        return new MutableMessageProperties().messageTypeProperties(persists
                ? MessageTypeProperties.BINARY_PERSIST
                : MessageTypeProperties.BINARY);
    }
}
