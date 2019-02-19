package com.playground.basicpaxos.proto;

import com.playground.basicpaxos.conn.Packet;

import java.nio.ByteBuffer;

public class PrepareReq extends AbstractData implements Data {
    private int version;

    private String name;

    public PrepareReq(Packet packet) {
        super(packet);
    }

    public PrepareReq(int sid, int version, String name) {
        super(sid, DataTypes.PREPARE);
        this.version = version;
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    @Override
    protected void parsePacketBytes(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        version = bb.getInt();
        byte[] nameBytes = new byte[bb.getInt()];
        bb.get(nameBytes);
        name = new String(nameBytes);
    }

    @Override
    protected byte[] toPacketBytes() {
        ByteBuffer bb = ByteBuffer.wrap(new byte[4 + 4 + name.getBytes().length]);
        bb.putInt(version);
        bb.putInt(name.getBytes().length);
        bb.put(name.getBytes());
        return bb.array();
    }
}
