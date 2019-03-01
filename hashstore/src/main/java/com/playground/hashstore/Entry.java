package com.playground.hashstore;

import java.nio.ByteBuffer;

public class Entry {

    public final String key;

    public final byte[] value;

    public Entry(String key, byte[] value) {
        this.key = key;
        this.value = value;
    }

    // read
    public static Entry read(ByteBuffer byteBuffer) {
        int cap = byteBuffer.capacity();
        int keyLen = byteBuffer.getInt();
        byte[] keyB = new byte[keyLen];
        byte[] value = new byte[cap - keyLen];
        byteBuffer.get(keyB);
        byteBuffer.get(value);
        return new Entry(new String(keyB), value);
    }

    // write
    private ByteBuffer writeBuf;

    synchronized public ByteBuffer getWriteBuf() {
        if (writeBuf == null) {
            prepareWriteBuf();
            writeBuf.flip();
        }
        return writeBuf;
    }

    private void prepareWriteBuf() {
        byte[] keyB = key.getBytes();
        int len = 4 + 4 + keyB.length + value.length;
        writeBuf = ByteBuffer.allocate(len);
        writeBuf.putInt(len);
        writeBuf.putInt(keyB.length);
        writeBuf.put(keyB);
        writeBuf.put(value);
    }
}
