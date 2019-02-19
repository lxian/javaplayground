package com.playground.basicpaxos.client.proto;

public class GetProposalReq implements Data {

    private String name;

    public GetProposalReq(String name) {
        this.name = name;
    }

    @Override
    public int getType() {
        return DataTypes.GET_PROPOSAL;
    }

    @Override
    public Packet toPacket() {
        return new Packet(getType(), name.getBytes());
    }

    @Override
    public void readPacket(Packet packet) {
        name = new String(packet.getBytes());
    }
}

