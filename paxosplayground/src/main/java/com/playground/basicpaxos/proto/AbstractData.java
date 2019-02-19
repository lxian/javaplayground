package com.playground.basicpaxos.proto;

import com.playground.basicpaxos.conn.Packet;

public abstract class AbstractData implements Data {

    private int sid;
    private int type;

    public AbstractData(Packet packet) {
        readPacket(packet);
    }

    public AbstractData(int sid, int type) {
        this.sid = sid;
        this.type = type;
    }

    @Override
    public int getSid() {
        return sid;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public Packet toPacket() {
        return new Packet(sid, type, toPacketBytes());
    }

    @Override
    public void readPacket(Packet packet) {
        this.sid = packet.getSid();
        this.type = packet.getType();
        parsePacketBytes(packet.getBytes());
    }

    protected abstract void parsePacketBytes(byte[] bytes);

    protected abstract byte[] toPacketBytes();
}
