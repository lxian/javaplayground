package com.playground.basicpaxos.processor;

import com.playground.basicpaxos.Config;
import com.playground.basicpaxos.conn.Packet;
import com.playground.basicpaxos.peer.Peer;
import com.playground.basicpaxos.proto.DataTypes;
import com.playground.basicpaxos.proto.PrepareReq;
import com.playground.basicpaxos.proto.ProposalAck;
import com.playground.basicpaxos.store.Proposal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class PrepareVersionProcessor extends Thread implements Processor {

    private Logger logger = LoggerFactory.getLogger(PrepareVersionProcessor.class);

    private boolean closing;
    private Processor next;
    private int sid;
    private Peer peer;
    private Config config;
    private ConcurrentHashMap<String, ProposalSubmission> submissions;

    public PrepareVersionProcessor(int sid, Peer peer, Config config) {
        super();
        this.sid = sid;
        this.peer = peer;
        this.peer.registerHandler(this);
        this.submissions = new ConcurrentHashMap<>();
        this.config = config;
    }

    @Override
    public void run() {
        while (!closing) {
            Iterator<ProposalSubmission> it = submissions.values().iterator();
            while (it.hasNext()) {
                ProposalSubmission submission = it.next();
                if (submission.getLastProposals().size() >= config.quorumSize()) {
                    it.remove();
                    Proposal highestVersioned = submission.highestVersionedLastProposal();
                    if (highestVersioned == Proposal.EMPTY_PROPOSAL) {
                        next.process(
                                new Proposal(
                                0,
                                submission.getProposal().getName(),
                                submission.getProposal().getValue()));
                    } else {
                        next.process(new Proposal(
                                highestVersioned.getVersion()+1,
                                submission.getProposal().getName(),
                                highestVersioned.getValue()));
                    }
                }
            }
        }
    }

    @Override
    public void process(Proposal proposal) {
        submissions.put(proposal.getName(), new ProposalSubmission(proposal));
        PrepareReq req = new PrepareReq(this.sid, proposal.getVersion(), proposal.getName());
        for (int sid: config.getServerIds()) {
            try {
                peer.send(sid, req.toPacket());
            } catch (IOException e) {
                logger.error("Error sending req {}", req, e);
            }
        }
    }

    @Override
    public void setNext(Processor processor) {
        this.next = processor;
    }

    @Override
    public void close() {
        this.closing = true;
    }

    @Override
    public void handle(Packet packet) {
        if (packet.getType() == DataTypes.PROPOSAL_ACK) {
            ProposalAck proposalAck = new ProposalAck(packet);
            ProposalSubmission submission = submissions.get(proposalAck.getProposal().getName());
            if (submission != null) {
                submission.addLastProposal(proposalAck.getSid(), proposalAck.getProposal());
            }
        }
    }
}
