package com.playground.basicpaxos.conn.bio;

import com.playground.basicpaxos.conn.Packet;

public class IncomingPacket {
    private Packet packet;
    private BIOWorker bioWorker;

    public IncomingPacket(Packet packet, BIOWorker bioWorker) {
        this.packet = packet;
        this.bioWorker = bioWorker;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public BIOWorker getBioWorker() {
        return bioWorker;
    }

    public void setBioWorker(BIOWorker bioWorker) {
        this.bioWorker = bioWorker;
    }
}
