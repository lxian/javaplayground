package com.playground.basicpaxos.conn;

import java.io.*;
import java.util.Arrays;

public class Packet {
    private int sid;
    private int type;
    private byte[] bytes;

    public Packet(int sid, int type) {
        this.sid = sid;
        this.type = type;
        this.bytes = new byte[0];
    }

    public Packet(int sid, int type, byte[] bytes) {
        this.sid = sid;
        this.type = type;
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static Packet read(InputStream in) throws IOException, PacketSerlizationError {
        DataInputStream din = new DataInputStream(in);
        int len = din.readInt();
        int sid = din.readInt();
        int type = din.readInt();
        byte[] bytes = new byte[len-8];
        if (din.read(bytes) == -1) {
            throw new PacketSerlizationError("incomplete packet");
        }
        return new Packet(sid, type, bytes);
    }

    public void write(OutputStream out) throws IOException {
        DataOutputStream din = new DataOutputStream(out);
        din.writeInt(bytes.length + 8);
        din.writeInt(sid);
        din.writeInt(type);
        din.write(bytes);
    }

    @Override
    public String toString() {
        return "Packet{" +
                "sid=" + sid +
                ", type=" + type +
                ", bytes=" + new String(bytes) +
                '}';
    }
}
