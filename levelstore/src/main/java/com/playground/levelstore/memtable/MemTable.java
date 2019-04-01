package com.playground.levelstore.memtable;

public interface MemTable {

    void put(String key, byte[] value);

    byte[] get(String key);

    long size();

    void travelThrough(MemTableEntryConsumer memTableEntryConsumer);

    void markImmutable();
}
