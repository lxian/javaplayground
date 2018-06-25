package com.lxian.playground.json.mapper.error;

public class JsonMappingError extends Exception {

    public JsonMappingError() {
    }

    public JsonMappingError(String message) {
        super(message);
    }

    public JsonMappingError(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonMappingError(Throwable cause) {
        super(cause);
    }

}
