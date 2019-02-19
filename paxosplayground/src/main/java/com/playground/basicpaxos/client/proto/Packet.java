package com.playground.basicpaxos.client.proto;


import java.io.*;
import java.nio.ByteBuffer;

public class Packet {
    private int type;
    private byte[] bytes;

    public Packet(int type) {
        this.type = type;
        this.bytes = new byte[0];
    }

    public Packet(int type, byte[] bytes) {
        this.type = type;
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static Packet read(ByteBuffer bb) {
        int len = bb.capacity();
        int type = bb.getInt();
        byte[] bytes = new byte[len-4];
        bb.get(bytes);
        return new Packet(type, bytes);
    }

    ByteBuffer bb;

    public ByteBuffer getBB() {
        if (bb == null) {
            createBB();
        }
        return bb;
    }

    public void createBB() {
        ByteBuffer bb = ByteBuffer.allocate(bytes.length + 4 + 4);
        bb.putInt(bytes.length + 4);
        bb.putInt(type);
        bb.put(bytes);
        bb.flip();
        this.bb = bb;
    }

    @Override
    public String toString() {
        return "Packet{" +
                ", type=" + type +
                ", bytes=" + new String(bytes) +
                '}';
    }
}
