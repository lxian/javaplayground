package com.lxian.playground.json.parser.error;

public class JsonParseError extends Exception {

    public JsonParseError() {
    }

    public JsonParseError(String message) {
        super(message);
    }

    public JsonParseError(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonParseError(Throwable cause) {
        super(cause);
    }

}
