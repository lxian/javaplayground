package com.playground.basicpaxos.proto;

import com.playground.basicpaxos.conn.Packet;
import com.playground.basicpaxos.store.Proposal;

public class AcceptProposalReq extends AbstractData {
    private Proposal proposal;

    public AcceptProposalReq(Packet packet) {
        super(packet);
    }

    public AcceptProposalReq(int sid, Proposal proposal) {
        super(sid, DataTypes.ACCEPT);
        this.proposal = proposal;
    }

    public Proposal getProposal() {
        return proposal;
    }

    @Override
    protected void parsePacketBytes(byte[] bytes) {
        this.proposal = Proposal.fromBytes(bytes);
    }

    @Override
    protected byte[] toPacketBytes() {
        return proposal.toBytes();
    }
}
