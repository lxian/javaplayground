package com.playground.basicpaxos.conn;

public interface PeerResponseHandler {
    void handle(Packet packet, PeerConnection connection);
}
