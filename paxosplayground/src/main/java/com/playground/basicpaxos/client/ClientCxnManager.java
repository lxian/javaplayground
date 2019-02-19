package com.playground.basicpaxos.client;

import com.playground.basicpaxos.Config;
import com.playground.basicpaxos.client.proto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ClientCxnManager {
    private Logger logger = LoggerFactory.getLogger(ClientCxnManager.class);

    private final int QUEUE_SIZE = 100;
    Config config;
    Map<Integer, ClientCxn> clientCxnMap;
    Map<Integer, ArrayBlockingQueue<Packet>> sendQueues;
    ArrayBlockingQueue<Packet> recvQueue;
    Selector selector;
    private boolean closing = false;
    private Poller poller;

    public ClientCxnManager(Config config) {
        this.config = config;
        recvQueue = new ArrayBlockingQueue<Packet>(QUEUE_SIZE);
    }

    public void start() throws IOException {
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
        poller = new Poller();
        poller.setDaemon(true);
        poller.start();
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

    public Data pollResponse() {
        final long POLL_TIME = 5000;
        Packet packet = null;
        try {
            packet = recvQueue.poll(POLL_TIME, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return null;
        }

        if (packet.getType() == DataTypes.PROPOSAL_ACK) {
            ProposalAck proposalAck = new ProposalAck();
            proposalAck.readPacket(packet);
            return proposalAck;
        }
        return null;
    }

    private class Poller extends Thread {

        @Override
        public void run() {
            final long seletTO = 2000;
            while (!closing) {
                try {
                    if (selector.select(seletTO) > 0) {
                        Set<SelectionKey> selected = selector.selectedKeys();

                        for (SelectionKey selectionKey : selected) {
                            if (selectionKey.isConnectable()) {

                            } else {
                                ClientCxn clientCxn = (ClientCxn) selectionKey.attachment();
                                if (selectionKey.isReadable()) {
                                    // read len
                                    if (clientCxn.readBuf == null) {
                                        if (clientCxn.lenBuf.hasRemaining()) {
                                            clientCxn.sc.read(clientCxn.lenBuf);
                                        } else {
                                            clientCxn.lenBuf.flip();
                                            clientCxn.readBuf = ByteBuffer.allocate(clientCxn.lenBuf.getInt());
                                        }
                                    }

                                    if (clientCxn.readBuf != null) {
                                        if (clientCxn.readBuf.hasRemaining()) {
                                            clientCxn.sc.read(clientCxn.readBuf);
                                        } else {
                                            Packet packet = Packet.read(clientCxn.readBuf);
                                            clientCxn.readBuf = null;
                                            clientCxn.lenBuf.clear();
                                            handleIncomingPacket(packet);
                                        }
                                    }
                                }
                                if (selectionKey.isWritable()) {
                                    Packet packet = clientCxn.sendQueue.peek();
                                    if (!packet.getBB().hasRemaining()) {
                                        clientCxn.sendQueue.poll();
                                    } else {
                                        clientCxn.selectionKey.interestOps(clientCxn.selectionKey.interestOps() | SelectionKey.OP_READ);
                                        clientCxn.sc.write(packet.getBB());
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    logger.error("Error selecting", e);
                }
            }
        }
    }
}
