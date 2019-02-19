package com.playground.basicpaxos;

import com.playground.basicpaxos.conn.Packet;
import com.playground.basicpaxos.peer.Peer;
import com.playground.basicpaxos.peer.PeerIncomingPacketHandler;
import com.playground.basicpaxos.processor.AcceptProcessor;
import com.playground.basicpaxos.processor.PrepareVersionProcessor;
import com.playground.basicpaxos.processor.ProposalGatheringProcessor;
import com.playground.basicpaxos.proto.*;
import com.playground.basicpaxos.store.Proposal;
import com.playground.basicpaxos.store.ProposalDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.playground.basicpaxos.proto.DataTypes.*;

public class Paxos implements PeerIncomingPacketHandler {
    private Logger logger = LoggerFactory.getLogger(Paxos.class);

    private int sid;
    private Peer peer;
    private final int QUEUE_SIZE = 100;
    private ProposalDB proposalDB;
    private final ProposalGatheringProcessor proposalGatheringProcessor;
    private final PrepareVersionProcessor prepareVersionProcessor;
    private final AcceptProcessor acceptProcessor;

    public Paxos(int sid, Config config) {
        this.sid = sid;
        proposalDB = new ProposalDB();
        peer = new Peer(sid, config);
        peer.registerHandler(this);
        proposalGatheringProcessor = new ProposalGatheringProcessor(sid, peer, config);
        prepareVersionProcessor = new PrepareVersionProcessor(sid, peer, config);
        proposalGatheringProcessor.setNext(prepareVersionProcessor);
        acceptProcessor = new AcceptProcessor(sid, peer, config);
        prepareVersionProcessor.setNext(acceptProcessor);
    }

    public void start() {
        proposalGatheringProcessor.start();
        prepareVersionProcessor.start();
        acceptProcessor.start();
        peer.start();
    }

    public void close() {
        acceptProcessor.close();
        prepareVersionProcessor.close();
        proposalGatheringProcessor.close();
        peer.close();
    }

    public void submitProposal(String name, byte[] val) {
        proposalGatheringProcessor.process(new Proposal(-1, name, val));
    }

    public ProposalDB getProposalDB() {
        return proposalDB;
    }

    public String dump() {
        return "SID:" + sid
                + proposalDB.dump()
                + peer.dump();
    }

    void send(int sid, Data data) {
        try {
            peer.send(sid, data.toPacket());
        } catch (IOException e) {
            logger.error("Error sending data {} to sid-{}", data, sid, e);
        }
    }

    @Override
    public void handle(Packet packet) {
        switch (packet.getType()) {
            case GET_PROPOSAL:
                GetProposalReq getProposalReq = new GetProposalReq(packet);
                send(getProposalReq.getSid(), new ProposalAck(this.sid, proposalDB.getProposal(getProposalReq.getName())));
                break;
            case PREPARE:
                PrepareReq prepareReq = new PrepareReq(packet);
                int promised = proposalDB.makePromise(prepareReq.getName(), prepareReq.getVersion());
                if (promised == prepareReq.getVersion()) {
                    send(prepareReq.getSid(), new ProposalAck(this.sid, proposalDB.getProposal(prepareReq.getName())));
                }
            case ACCEPT:
                AcceptProposalReq acceptProposalReq = new AcceptProposalReq(packet);
                Proposal toAccept = acceptProposalReq.getProposal();
                proposalDB.commitProposal(toAccept);
            default:
                break;
        }
    }
}
