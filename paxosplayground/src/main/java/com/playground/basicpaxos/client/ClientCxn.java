package com.playground.basicpaxos.client;

import com.playground.basicpaxos.client.proto.Packet;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;

public class ClientCxn {
    int sid;
    SocketChannel sc;
    InetSocketAddress addr;
    ArrayBlockingQueue<Packet> sendQueue;
    SelectionKey selectionKey;

    ByteBuffer lenBuf = ByteBuffer.allocate(4);
    ByteBuffer readBuf;

    public ClientCxn(int sid, SocketChannel sc, InetSocketAddress addr, ArrayBlockingQueue<Packet> packetQueue, SelectionKey selectionKey) {
        this.sid = sid;
        this.sc = sc;
        this.addr = addr;
        this.sendQueue = packetQueue;
        this.selectionKey = selectionKey;
    }

}
