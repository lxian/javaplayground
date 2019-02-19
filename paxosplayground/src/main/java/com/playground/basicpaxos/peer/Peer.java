package com.playground.basicpaxos.peer;

import com.playground.basicpaxos.Config;
import com.playground.basicpaxos.conn.Packet;
import com.playground.basicpaxos.conn.PeerConnection;
import com.playground.basicpaxos.conn.PeerResponseHandler;
import com.playground.basicpaxos.conn.bio.BIOPeerConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Peer implements PeerResponseHandler {
    private PeerConnection peerConnection;
    private Config config;
    private boolean closing;
    private int sid;
    private Logger logger = LoggerFactory.getLogger(Peer.class);
    private List<PeerIncomingPacketHandler> handlers;

    public Peer(int sid, Config config) {
        this.sid = sid;
        this.handlers = new LinkedList<>();
        this.config = config;
        this.peerConnection = new BIOPeerConnection(sid, config);
        this.peerConnection.registerHandler(this);
    }

    public void start() {
        try {
            peerConnection.start();
        } catch (IOException e) {
            logger.error("Failed to start Peer {}", sid, e);
        }

//        while (!closing) {
//            try {
//                for (int sid : config.getServerIds()) {
//                    if (sid != this.sid) {
//                        peerConnection.send(sid, new Packet(this.sid, -2, ("Hello from " + this.sid).getBytes()));
//                    }
//                }
//            } catch (IOException e) {
//            }
//
//            try {
//                Thread.sleep(new Random().nextInt(2000) + 1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public void close() {
        closing = true;
        peerConnection.close();
    }

    public void send(int sid, Packet packet) throws IOException {
        logger.debug(">>>>SID-{}: sending packet {} to {}", this.sid, packet, sid);
        if (sid == this.sid) {
            handle(packet, peerConnection);
        } else {
            peerConnection.send(sid, packet);
        }
    }

    @Override
    public void handle(Packet packet, PeerConnection connection) {
        logger.info(">>>>SID-{}: recived packet {}", sid, packet);
        for (PeerIncomingPacketHandler handler: handlers) {
            try {
                handler.handle(packet);
            } catch (Throwable e) {
                logger.error("Error handling packet {}, handler {}", packet, handler, e);
            }
        }
    }

    public Config getConfig() {
        return config;
    }

    public void registerHandler(PeerIncomingPacketHandler handler) {
        handlers.add(handler);
    }

    public String dump() {
        return peerConnection.dump();
    }
}
