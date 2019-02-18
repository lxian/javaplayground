package com.lxian.playground.json.mapper;

import com.lxian.playground.json.mapper.error.JsonDeserializationError;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public abstract class AbstractJsonDeserializer implements JsonDeserializer {

    public <T> T read(String value, Class<T> targetClazz) throws IOException, JsonDeserializationError {
        return read(new ByteArrayInputStream(value.getBytes()), targetClazz);
    }

}
