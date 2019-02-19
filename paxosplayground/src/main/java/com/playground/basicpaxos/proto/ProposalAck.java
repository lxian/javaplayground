package com.playground.basicpaxos.proto;

import com.playground.basicpaxos.conn.Packet;
import com.playground.basicpaxos.store.Proposal;

public class ProposalAck extends AbstractData {
    private Proposal proposal;

    public ProposalAck(Packet packet) {
        super(packet);
    }

    public ProposalAck(int sid, Proposal proposal) {
        super(sid, DataTypes.PROPOSAL_ACK);
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
