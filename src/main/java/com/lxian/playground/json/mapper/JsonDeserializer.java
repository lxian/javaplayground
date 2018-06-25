package com.lxian.playground.json.mapper;

import com.lxian.playground.json.mapper.error.JsonDeserializationError;
import com.lxian.playground.json.parser.JsonParser;
import com.lxian.playground.json.parser.error.InvalidJsonError;

import java.io.IOException;
import java.io.InputStream;

public interface JsonDeserializer {

    <T> T read(InputStream in, Class<T> targetClazz) throws IOException, JsonDeserializationError;

}
