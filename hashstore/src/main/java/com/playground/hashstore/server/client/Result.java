package com.playground.hashstore.server.client;

public class Result {

    public final boolean success;

    public final byte[] value;

    public final HashStoreError error;

    public Result(boolean success, byte[] value, HashStoreError error) {
        this.success = success;
        this.value = value;
        this.error = error;
    }
}
