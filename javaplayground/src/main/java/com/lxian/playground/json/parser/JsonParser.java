package com.lxian.playground.json.parser;

import com.lxian.playground.json.parser.error.InvalidJsonError;
import com.lxian.playground.json.parser.object.JsObject;

import java.io.IOException;
import java.io.InputStream;

public interface JsonParser {

    JsObject parse(InputStream in) throws IOException, InvalidJsonError;

}
