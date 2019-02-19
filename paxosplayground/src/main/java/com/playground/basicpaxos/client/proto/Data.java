package com.playground.basicpaxos.client.proto;

public interface Data {
    int getType();
    Packet toPacket();
    void readPacket(Packet packet);
}
