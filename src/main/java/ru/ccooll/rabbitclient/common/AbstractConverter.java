package ru.ccooll.rabbitclient.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.ccooll.rabbitclient.error.ErrorHandler;

@RequiredArgsConstructor
@Setter
@Getter
@Accessors(fluent = true, chain = false)
public abstract class AbstractConverter<S extends Serializer,
        D extends Deserializer> implements Converter {

    protected final S serializer;
    protected final D deserializer;
    protected ErrorHandler errorHandler;
}
