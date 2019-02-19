package com.playground.basicpaxos.processor;

import com.playground.basicpaxos.Config;
import com.playground.basicpaxos.conn.Packet;
import com.playground.basicpaxos.peer.Peer;
import com.playground.basicpaxos.proto.DataTypes;
import com.playground.basicpaxos.proto.GetProposalReq;
import com.playground.basicpaxos.proto.ProposalAck;
import com.playground.basicpaxos.store.Proposal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class ProposalGatheringProcessor extends Thread implements Processor {

    private Logger logger = LoggerFactory.getLogger(ProposalGatheringProcessor.class);

    private boolean closing;
    private Processor next;
    private int sid;
    private Peer peer;
    private Config config;
    private ConcurrentHashMap<String, ProposalSubmission> submissions;

    public ProposalGatheringProcessor(int sid, Peer peer, Config config) {
        super();
        this.sid = sid;
        this.peer = peer;
        this.peer.registerHandler(this);
        this.submissions = new ConcurrentHashMap<>();
        this.config = config;
    }

    private final Object lock = new Object();
    @Override
    public void run() {
        while (!closing) {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Iterator<ProposalSubmission> it = submissions.values().iterator();
            while (it.hasNext()) {
                ProposalSubmission submission = it.next();
                logger.debug("Proposal {} gathered size {}", submission.getName(), submission.getLastProposals().size());
                if (submission.getLastProposals().size() >= config.quorumSize()) {
                    it.remove();
                    next.process(
                            new Proposal(
                                    submission.highestVersionedLastProposal().getVersion(),
                                    submission.getName(),
                                    submission.getVal()
                            )
                    );
                }
            }
        }
    }

    @Override
    public void process(Proposal proposal) {
        submissions.put(proposal.getName(), new ProposalSubmission(proposal));
        GetProposalReq req = new GetProposalReq(this.sid, proposal.getName());
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
            logger.debug("Gathered proposal {} from sid-{}", proposalAck.getProposal(), packet.getSid());
            ProposalSubmission submission = submissions.get(proposalAck.getProposal().getName());
            if (submission != null) {
                submission.addLastProposal(proposalAck.getSid(), proposalAck.getProposal());
            }

            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }
}
