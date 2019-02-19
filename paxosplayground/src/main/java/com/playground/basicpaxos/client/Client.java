package com.playground.basicpaxos.client;

import com.playground.basicpaxos.Config;
import com.playground.basicpaxos.client.proto.Data;
import com.playground.basicpaxos.client.proto.DataTypes;
import com.playground.basicpaxos.client.proto.ProposalAck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {

    private boolean closing = false;
    Config config;

    public Client(Config config) {
        this.config = config;
    }

    public void close() {
        closing = true;
    }

    void start() throws IOException {

        ClientCxnManager clientCxnManager = new ClientCxnManager(config);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (!closing) {
            System.out.print("> ");
            String input = reader.readLine();
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
                    clientCxnManager.submitProposal(name, val.getBytes());
                    break;
                }
                case "GET": {
                    String name = inputSegs[1];
                    clientCxnManager.getProposal(name);
                    break;
                }
                default:
                    break;
            }

            Data data = clientCxnManager.pollResponse();
            if (data == null) {
                continue;
            }
            if (data.getType() == DataTypes.PROPOSAL_ACK) {
                System.out.println(new String(((ProposalAck) data).getValue()));
            }
        }
    }
}
