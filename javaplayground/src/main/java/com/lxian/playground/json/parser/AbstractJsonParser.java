package com.lxian.playground.json.parser;

import com.lxian.playground.json.parser.error.InvalidJsonError;
import com.lxian.playground.json.parser.object.JsObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public abstract class AbstractJsonParser implements JsonParser {

    public JsObject parse(byte[] bytes) throws IOException, InvalidJsonError {
        return parse(new ByteArrayInputStream(bytes));
    }

    public JsObject parse(String string) throws IOException, InvalidJsonError {
        return parse(string.getBytes());
    }
}
