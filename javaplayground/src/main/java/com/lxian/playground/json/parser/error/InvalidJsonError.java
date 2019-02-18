package com.lxian.playground.json.parser.error;

public class InvalidJsonError extends JsonParseError {

    public InvalidJsonError(int errorIdx) {
        super(String.format("input JSON is invalid at pos %d", errorIdx));
    }

    public InvalidJsonError(String message) {
        super(message);
    }
}
