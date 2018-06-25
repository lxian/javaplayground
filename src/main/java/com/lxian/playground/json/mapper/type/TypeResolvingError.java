package com.lxian.playground.json.mapper.type;

public class TypeResolvingError extends Exception {

    public TypeResolvingError() {
    }

    public TypeResolvingError(String message) {
        super(message);
    }

    public TypeResolvingError(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeResolvingError(Throwable cause) {
        super(cause);
    }
}
