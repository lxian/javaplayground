package com.playground.levelstore.memtable;

public interface MemTableEntryConsumer {
    void consumer(String key, byte[] value);
}
