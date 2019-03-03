package com.playground.hashstore.error;

public class ConfigParseError extends Exception {

    public ConfigParseError(String message) {
        super(message);
    }

    public ConfigParseError(String message, Throwable cause) {
        super(message, cause);
    }
}
