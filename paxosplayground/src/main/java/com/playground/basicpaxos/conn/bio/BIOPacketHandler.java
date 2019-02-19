package com.playground.basicpaxos.conn.bio;

public interface BIOPacketHandler {
    void workerClosed(BIOWorker worker);
}
