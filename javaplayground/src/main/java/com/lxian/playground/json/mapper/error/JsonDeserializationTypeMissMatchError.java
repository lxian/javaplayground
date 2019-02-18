package com.lxian.playground.json.mapper.error;

public class JsonDeserializationTypeMissMatchError extends JsonDeserializationError {

    public JsonDeserializationTypeMissMatchError() {
    }

    public JsonDeserializationTypeMissMatchError(String message) {
        super(message);
    }

    public JsonDeserializationTypeMissMatchError(String message, Throwable cause) {
        super(message, cause);
    }
}
