package com.playground.basicpaxos.proto;

import com.playground.basicpaxos.conn.Packet;

public interface Data {
    int getSid();
    int getType();
    Packet toPacket();
    void readPacket(Packet packet);
}
