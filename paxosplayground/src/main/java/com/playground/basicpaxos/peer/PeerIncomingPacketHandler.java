package com.playground.basicpaxos.peer;

import com.playground.basicpaxos.conn.Packet;
import com.playground.basicpaxos.conn.PeerConnection;

public interface PeerIncomingPacketHandler {
    void handle(Packet packet);
}
