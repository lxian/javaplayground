package com.playground.basicpaxos.store;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Proposal {
    private int version;
    private String name; // name as the proposal identifier
    private byte[] value;

    public static int EMPTY_PROPOSAL_VERSION = -1;
    public static Proposal EMPTY_PROPOSAL = new Proposal(EMPTY_PROPOSAL_VERSION, "", new byte[0]);
    public static Proposal emptyProposal(String name) {
        return new Proposal(EMPTY_PROPOSAL_VERSION, name, new byte[0]);
    }

    public Proposal(int version, String name, byte[] value) {
        this.version = version;
        this.name = name;
        this.value = value;
    }

    public int getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public byte[] getValue() {
        return value;
    }

    public byte[] toBytes() {
        ByteBuffer bb = ByteBuffer.allocate(4+4+name.getBytes().length+4+value.length);
        bb.putInt(version);
        bb.putInt(name.getBytes().length);
        bb.put(name.getBytes());
        bb.putInt(value.length);
        bb.put(value);
        return bb.array();
    }

    public static Proposal fromBytes(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        int version;
        int nameLen;
        int valueLen;
        version = bb.getInt();
        nameLen = bb.getInt();
        byte[] name = new byte[nameLen];
        bb.get(name);
        valueLen = bb.getInt();
        byte[] value = new byte[valueLen];
        bb.get(value);
        return new Proposal(
                version,
                new String(name),
                value
        );
    }

    @Override
    public String toString() {
        return "Proposal{" +
                "version=" + version +
                ", name='" + name +
                ", value=" + Arrays.toString(value) +
                '}';
    }
}
