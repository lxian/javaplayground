package com.playground.basicpaxos.processor;

import com.playground.basicpaxos.peer.PeerIncomingPacketHandler;
import com.playground.basicpaxos.store.Proposal;

public interface Processor extends PeerIncomingPacketHandler {
    void setNext(Processor processor);
    void process(Proposal proposal);
    void start();
    void close();
}
