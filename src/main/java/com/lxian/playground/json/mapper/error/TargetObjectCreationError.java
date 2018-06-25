package com.lxian.playground.json.mapper.error;

public class TargetObjectCreationError extends JsonDeserializationError {

    public TargetObjectCreationError() {
    }

    public TargetObjectCreationError(String message) {
        super(message);
    }

    public TargetObjectCreationError(String message, Throwable cause) {
        super(message, cause);
    }

    public TargetObjectCreationError(Throwable cause) {
        super(cause);
    }
}
