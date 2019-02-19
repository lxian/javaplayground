package com.playground.basicpaxos.processor;

import com.playground.basicpaxos.Config;
import com.playground.basicpaxos.conn.Packet;
import com.playground.basicpaxos.peer.Peer;
import com.playground.basicpaxos.proto.AcceptProposalReq;
import com.playground.basicpaxos.proto.DataTypes;
import com.playground.basicpaxos.proto.PrepareReq;
import com.playground.basicpaxos.proto.ProposalAck;
import com.playground.basicpaxos.store.Proposal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class AcceptProcessor implements Processor {

    private Logger logger = LoggerFactory.getLogger(AcceptProcessor.class);

    private int sid;
    private Peer peer;
    private Config config;

    public AcceptProcessor(int sid, Peer peer, Config config) {
        super();
        this.sid = sid;
        this.peer = peer;
        this.peer.registerHandler(this);
        this.config = config;
    }

    @Override
    public void process(Proposal proposal) {
        AcceptProposalReq req = new AcceptProposalReq(this.sid, proposal);
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
    }

    @Override
    public void start() {
    }

    @Override
    public void close() {
    }

    @Override
    public void handle(Packet packet) {
    }
}
