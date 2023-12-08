package ru.ccooll.rabbitclient.common.simple;

import ru.ccooll.rabbitclient.common.AbstractConverter;

public class SimpleConverter extends AbstractConverter<SimpleSerializer, SimpleDeserializer> {

    public SimpleConverter() {
        super(new SimpleSerializer(), new SimpleDeserializer());
    }
}
