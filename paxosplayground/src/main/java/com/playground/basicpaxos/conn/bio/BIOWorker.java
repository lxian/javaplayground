package com.playground.basicpaxos.conn.bio;

import com.playground.basicpaxos.conn.Packet;
import com.playground.basicpaxos.conn.PacketSerlizationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

public class BIOWorker {

    private Logger logger = LoggerFactory.getLogger(BIOWorker.class);

    private class ReaderThread extends Thread {
        @Override
        public void run() {
            while (!closing && !socket.isClosed()) {
                Packet packet = null;
                try {
                    packet = Packet.read(socket.getInputStream());
                    if (packet != null) {
                        logger.debug("Received packet {}", packet);
                        recvQueue.offer(new IncomingPacket(packet, BIOWorker.this));
                    }
                } catch (IOException | PacketSerlizationError e) {
                    logger.error("Error sending packet {}", packet, e);
                    BIOWorker.this.close();
                    return;
                }
            }
        }
    }

    private class WriterThread extends Thread {
        @Override
        public void run() {
            while (!closing && !socket.isClosed()) {
                Packet packet = sendQueue.poll();
                try {
                    if (packet != null) {
                        logger.debug("Writing packet {}", packet);
                        packet.write(socket.getOutputStream());
                        socket.getOutputStream().flush();
                    }
                } catch (IOException e) {
                    BIOWorker.this.close();
                    logger.error("Error sending packet {}", packet, e);
                }
            }
        }
    }

    private Socket socket;
    private boolean closing;

    private ReaderThread readerThread;
    private WriterThread writerThread;
    private ArrayBlockingQueue<Packet> sendQueue;
    private ArrayBlockingQueue<IncomingPacket> recvQueue;
    private BIOPacketHandler handler;

    public BIOWorker(Socket socket) {
        this.socket = socket;
    }

    public BIOWorker(BIOPacketHandler handler, ArrayBlockingQueue<IncomingPacket> recvQueue) {
        this.handler = handler;
        this.recvQueue = recvQueue;
    }

    public BIOWorker(BIOPacketHandler handler, ArrayBlockingQueue<Packet> sendQueue, ArrayBlockingQueue<IncomingPacket> recvQueue) {
        this.handler = handler;
        this.sendQueue = sendQueue;
        this.recvQueue = recvQueue;
    }

    public void setSendQueue(ArrayBlockingQueue<Packet> sendQueue) {
        this.sendQueue = sendQueue;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void start() {
        startRead();
        startWrite();
    }

    public void startRead() {
        readerThread = new ReaderThread();
        readerThread.setDaemon(true);
        readerThread.start();
    }

    public void startWrite() {
        writerThread = new WriterThread();
        writerThread.setDaemon(true);
        writerThread.start();
    }

    public void close() {
        closing = true;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        handler.workerClosed(this);
    }

    public void queuePacket(Packet packet) {
        sendQueue.offer(packet);
    }

}
