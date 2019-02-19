package com.playground.basicpaxos.client.proto;

public class ProposalAck implements Data {
    private byte[] value;

    public ProposalAck() {
    }

    public ProposalAck(byte[] value) {
        this.value = value;
    }


    public byte[] getValue() {
        return value;
    }

    @Override
    public int getType() {
        return DataTypes.GET_PROPOSAL;
    }

    @Override
    public Packet toPacket() {
        return new Packet(getType(), value);
    }

    @Override
    public void readPacket(Packet packet) {
        value = packet.getBytes();
    }
}

