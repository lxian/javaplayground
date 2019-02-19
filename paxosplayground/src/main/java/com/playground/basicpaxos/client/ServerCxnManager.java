package com.playground.basicpaxos.client;

import com.playground.basicpaxos.Config;
import com.playground.basicpaxos.client.proto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ServerCxnManager {
    private Logger logger = LoggerFactory.getLogger(ServerCxnManager.class);

    private final int QUEUE_SIZE = 100;
    int port;
    Config config;
    ServerSocketChannel serverSocketChannel;
    Map<Integer, ClientCxn> clientCxnMap;
    Map<Integer, ArrayBlockingQueue<Packet>> sendQueues;
    ArrayBlockingQueue<Packet> recvQueue;
    Selector selector;
    private boolean closing = false;

    public ServerCxnManager(int port, Config config) {
        this.port = port;
        this.config = config;
        recvQueue = new ArrayBlockingQueue<Packet>(QUEUE_SIZE);
    }

    public void start() throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress("0.0.0.0", port));
        selector = Selector.open();
        clientCxnMap = new HashMap<>();
        for (Map.Entry<Integer, InetSocketAddress> e : config.getClientAddrMap().entrySet()) {
            int sid = e.getKey();
            InetSocketAddress addr = e.getValue();
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(addr);
            socketChannel.configureBlocking(false);
            socketChannel.socket().setTcpNoDelay(true);
            socketChannel.socket().setSoLinger(false, -1);

            SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_CONNECT);

            ArrayBlockingQueue<Packet> sendQueue = new ArrayBlockingQueue<Packet>(QUEUE_SIZE);
            ClientCxn clientCxn = new ClientCxn(sid, socketChannel, addr, sendQueue, selectionKey);
            selectionKey.attach(clientCxn);
            sendQueues.put(sid, sendQueue);
            clientCxnMap.put(sid, clientCxn);
        }
    }

    public void close() {
        closing = true;
        for (ClientCxn client : clientCxnMap.values()) {
            try {
                client.sc.close();
            } catch (IOException e) {
                logger.error("Error closing sc", e);
            }
        }
        try {
            selector.close();
        } catch (IOException e) {
            logger.error("Error closing selector", e);
        }
    }

    private Random random = new Random();

    private void queuePacket(int idx, Packet packet) {
        sendQueues.get(idx).offer(packet);
        SelectionKey key = clientCxnMap.get(idx).selectionKey;
        key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
    }

    public void submitProposal(String name, byte[] content) {
        SubmitProposalReq req = new SubmitProposalReq(name, content);
        int sIdx = random.nextInt(sendQueues.values().size());
        queuePacket(sIdx, req.toPacket());
    }

    public void getProposal(String name) {
        GetProposalReq req = new GetProposalReq(name);
        for (int sid: clientCxnMap.keySet()) {
            queuePacket(sid, req.toPacket());
        }
    }

    public void handleIncomingPacket(Packet packet) {
        recvQueue.offer(packet);
    }

    private final long acceptTO = 2000;
    private final long selectTO = 2000;
    private Set<SocketChannel> clientSocks = new HashSet<>();

    private class Acceptor extends Thread {
        @Override
        public void run() {
            while (!closing) {
                try {
                    SocketChannel accepted = serverSocketChannel.accept();
                    accepted.configureBlocking(false);
                    accepted.socket().setTcpNoDelay(true);
                    accepted.socket().setSoLinger(false, -1);
                    accepted.register(selector, SelectionKey.OP_READ);
                    clientSocks.add(accepted);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class Poller extends Thread {

        @Override
        public void run() {
            while (!closing) {
            }
        }
    }
}
