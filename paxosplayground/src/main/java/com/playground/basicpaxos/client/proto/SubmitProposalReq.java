package com.playground.basicpaxos.client.proto;

import java.nio.ByteBuffer;

public class SubmitProposalReq implements Data {

    private String name;

    private byte[] value;

    public SubmitProposalReq(String name, byte[] value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public int getType() {
        return DataTypes.SUBMIT_PROPOSAL;
    }

    @Override
    public Packet toPacket() {
        byte[] bytes = new byte[4 + name.getBytes().length + 4 + value.length];
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        bb.putInt(name.getBytes().length);
        bb.put(name.getBytes());
        bb.putInt(value.length);
        bb.put(value);
        return new Packet(getType(), bb.array());
    }

    @Override
    public void readPacket(Packet packet) {
        ByteBuffer bb = ByteBuffer.wrap(packet.getBytes());
        int nameLen = bb.getInt();
        byte[] nameb = new byte[nameLen];
        bb.get(nameb);
        name = new String(nameb);

        int vLen = bb.getInt();
        value = new byte[vLen];
        bb.get(value);
    }
}
