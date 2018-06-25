package com.lxian.playground.json.mapper.error;

public class JsonDeserializationError extends JsonMappingError {

    public JsonDeserializationError() {
    }

    public JsonDeserializationError(String message) {
        super(message);
    }

    public JsonDeserializationError(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonDeserializationError(Throwable cause) {
        super(cause);
    }
}
