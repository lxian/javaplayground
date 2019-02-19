package com.playground.basicpaxos.proto;

import com.playground.basicpaxos.conn.Packet;

public class GetProposalReq extends AbstractData implements Data {
    String name;

    public GetProposalReq(Packet packet) {
        super(packet);
    }

    public GetProposalReq(int sid, String name) {
        super(sid, DataTypes.GET_PROPOSAL);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    protected void parsePacketBytes(byte[] bytes) {
        name = new String(bytes);
    }

    @Override
    protected byte[] toPacketBytes() {
        return this.name.getBytes();
    }
}
