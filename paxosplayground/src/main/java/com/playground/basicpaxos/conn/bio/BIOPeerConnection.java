package com.playground.basicpaxos.conn.bio;

import com.playground.basicpaxos.conn.Packet;
import com.playground.basicpaxos.Config;
import com.playground.basicpaxos.conn.PeerConnection;
import com.playground.basicpaxos.conn.PeerResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import static com.playground.basicpaxos.proto.Controls.INIT_CONN;

public class BIOPeerConnection implements PeerConnection, BIOPacketHandler {
    private Logger logger = LoggerFactory.getLogger(BIOPeerConnection.class);
    private int sid;
    private ServerSocket ss;
    private PeerResponseHandler handler;
    private boolean closing = false;
    private Config config;

    private AcceptorThread acceptorThread;
    private IncomingPacketPoller incomingPacketPoller;
    private ConcurrentHashMap<Integer, BIOWorker> workerMap;

    private int QUEUE_CAP = 100;
    private ConcurrentHashMap<Integer, ArrayBlockingQueue<Packet>> sendQueueMap = new ConcurrentHashMap<>();
    private ArrayBlockingQueue<IncomingPacket> recvQueue = new ArrayBlockingQueue<IncomingPacket>(QUEUE_CAP);

    private ArrayBlockingQueue<Packet> getSendQueue(Integer sid) {
        ArrayBlockingQueue<Packet> q = sendQueueMap.get(sid);
        if (q != null) {
            return q;
        } else {
            ArrayBlockingQueue<Packet> newQ = new ArrayBlockingQueue<Packet>(QUEUE_CAP);
            sendQueueMap.putIfAbsent(sid, newQ);
            return newQ;
        }
    }

    private class AcceptorThread extends Thread {
        private ServerSocket ss;

        public AcceptorThread(ServerSocket ss) {
            this.ss = ss;
        }

        @Override
        public void run() {
            while (!closing && !ss.isClosed()) {
                try {
                    Socket accepted = ss.accept();
                    if (accepted != null) {
                        BIOWorker bioWorker = new BIOWorker(BIOPeerConnection.this, recvQueue);
                        bioWorker.setSocket(accepted);
                        bioWorker.startRead();
                    }
                } catch (IOException e) {
                    logger.error("Error when accept socket", e);
                }
            }
        }
    }

    private class IncomingPacketPoller extends Thread {
        public void run() {
            while (!closing) {
                IncomingPacket packet = recvQueue.poll();
                try {
                    if (packet != null) {
                        BIOPeerConnection.this.handle(packet);
                    }
                } catch (Throwable e) {
                    logger.error("Error handling packet {}", packet.getPacket(), e);
                }
            }
        }
    }

    public BIOPeerConnection(int sid, Config config) {
        this.sid = sid;
        this.config = config;
        workerMap = new ConcurrentHashMap<>();
    }

    private InetSocketAddress getSockddress(int sid) {
        return config.getPeerAddrMap().get(sid);
    }

    @Override
    public void start() throws IOException {
        ss = new ServerSocket(getSockddress(sid).getPort());
        acceptorThread = new AcceptorThread(ss);
        acceptorThread.setDaemon(true);
        acceptorThread.start();
        incomingPacketPoller = new IncomingPacketPoller();
        incomingPacketPoller.setDaemon(true);
        incomingPacketPoller.start();
    }

    @Override
    public void connect(int sid) throws IOException {
        if (workerMap.containsKey(sid)) {
            return;
        }
        BIOWorker worker = new BIOWorker(this, getSendQueue(sid), recvQueue);
        Socket socket = new Socket(getSockddress(sid).getAddress(), getSockddress(sid).getPort());
        new Packet(this.sid, INIT_CONN).write(socket.getOutputStream());
        socket.getOutputStream().flush();

        if (sid > this.sid) {
            socket.close();
        } else {
            if (workerMap.putIfAbsent(sid, worker) == null) {
                worker.setSocket(socket);
                worker.start();
            } else {
                socket.close();
            }
        }
    }

    @Override
    public void send(int sid, Packet packet) throws IOException {
        getSendQueue(sid).offer(packet);
        connect(sid);
    }

    @Override
    public void registerHandler(PeerResponseHandler handler) {
        this.handler = handler;
    }

    @Override
    public void close() {
        try {
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (BIOWorker worker: workerMap.values()) {
            worker.close();
        }
        closing = true;
    }

    public void handle(IncomingPacket incomingPacket) {
        // handle controls
        BIOWorker worker = incomingPacket.getBioWorker();
        Packet packet = incomingPacket.getPacket();
        if (packet.getType() == INIT_CONN) {
            if (packet.getSid() < sid) {
                worker.close();
                logger.debug("SID-{} drop connection with {}", sid, packet.getSid());
                try {
                    logger.debug("SID-{} start connection to {}", sid, packet.getSid());
                    connect(packet.getSid());
                } catch (IOException e) {
                    logger.error("Error connecting to server " + sid, e);
                }
            } else {
                logger.debug("SID-{} added incoming connection with {}", sid, packet.getSid());
                if (workerMap.putIfAbsent(packet.getSid(), worker) == null) {
                    worker.setSendQueue(sendQueueMap.get(packet.getSid()));
                    worker.startWrite();
                } else {
                    worker.close();
                }
            }
        } else {
            // OTW handle over data packet
            this.handler.handle(packet, this);
        }
    }

    @Override
    public void workerClosed(BIOWorker worker) {
        Integer sid = null;
        for (Map.Entry<Integer, BIOWorker> e: this.workerMap.entrySet()) {
            if (e.getValue() == worker) {
                sid = e.getKey();
                break;
            }
        }
        if (sid != null) {
            workerMap.remove(sid);
        }
    }

    public String dump() {
        String s = "CONNECTED:";
        Enumeration<Integer> integerEnumeration = workerMap.keys();
        while(integerEnumeration.hasMoreElements()) {
            s += integerEnumeration.nextElement() + ", ";
        }
        return s;
    }

}
