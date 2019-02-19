package com.playground.basicpaxos;

import com.playground.basicpaxos.client.proto.Data;
import com.playground.basicpaxos.client.proto.DataTypes;
import com.playground.basicpaxos.client.proto.ProposalAck;
import com.playground.basicpaxos.conn.bio.BIOPeerConnection;
import com.playground.basicpaxos.peer.Peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

public class PaxosMain {

    // expecting
    // myid=1 server1:xxx.xxx.xx.xx:8888:9999,server2:xxx.xxx.xxx.xx:8888:9999
    // myid=1 server1:0.0.0.0:8881:9991,server2:0.0.0.0:8882:9992,server3:0.0.0.0:8883:9993
    public static void main(String[] args) throws IOException {
        int sid = Integer.parseInt(args[0].substring("myid=".length()));
        Config config = Config.parse(args[1]);

        Paxos paxos = new Paxos(sid, config);
        paxos.start();
//        Thread peerThread = new Thread(paxos::start);
//        peerThread.setDaemon(true);
//        peerThread.start();

        boolean closing = false;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (!closing) {
            System.out.print("> ");
            String input = null;
            input = reader.readLine();
            System.out.println();
            String[] inputSegs = input.split(" ");
            if (inputSegs.length == 0) {
                continue;
            }

            String cmd = inputSegs[0].toUpperCase();
            switch (cmd) {
                case "SUBMIT": {
                    String name = inputSegs[1];
                    String val = inputSegs[2];
                    paxos.submitProposal(name, val.getBytes());
                    break;
                }
                case "GETALL": {
                    System.out.println(paxos.dump());
                    break;
                }
                case "CLOSE": {
                    closing = true;
                    break;
                }
                default:
                    break;
            }
        }
        paxos.close();
    }
}
