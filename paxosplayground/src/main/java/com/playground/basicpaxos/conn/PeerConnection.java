package com.playground.basicpaxos.conn;

import java.io.IOException;

public interface PeerConnection {
    void registerHandler(PeerResponseHandler handler);
    void start() throws IOException;
    void connect(int sid) throws IOException;
    void send(int sid, Packet packet) throws IOException;
    void close();
    String dump();
}
