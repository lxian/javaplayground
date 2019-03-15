package com.playground.hashstore;

import com.playground.hashstore.config.ConfigProvider;
import com.playground.hashstore.error.ConfigParseError;
import com.playground.hashstore.server.client.HashStoreClient;
import com.playground.hashstore.server.client.HashStoreError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HashStoreClientMain {

    public static void main(String[] args) throws ConfigParseError, IOException {
        String host = null;
        int port = 0;
        for (String arg: args) {
            if (arg.startsWith("conf=")) {
                ConfigProvider.parseConfigFromFile(arg.substring("conf=".length()));
            }
            if (arg.startsWith("server=")) {
                arg = arg.substring("server=".length());
                host = arg.split(":")[0];
                port = Integer.valueOf(arg.split(":")[1]);
            }
        }
        HashStoreClient hashStoreClient = new HashStoreClient(host, port);
        hashStoreClient.start();

        boolean closing = false;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (!closing) {
            System.out.print("> ");
            String input = reader.readLine();
            String[] inputSegs = input.split(" ");
            if (inputSegs.length == 0) {
                continue;
            }

            String cmd = inputSegs[0].toLowerCase();
            try {
                switch (cmd) {
                    case "get": {
                        if (inputSegs.length >= 2) {
                            String name = inputSegs[1];
                            byte[] val = hashStoreClient.get(name);
                            System.out.println(val.length == 0 ? "NULL" : new String(val));
                        }
                        break;
                    }
                    case "set": {
                        if (inputSegs.length >= 3) {
                            String name = inputSegs[1];
                            String val = inputSegs[2];
                            hashStoreClient.set(name, val.getBytes());
                            System.out.println("OK");
                        }
                        break;
                    }
                    case "close": {
                        closing = true;
                        break;
                    }
                    default:
                        break;
                }
            } catch (HashStoreError e) {
                e.printStackTrace();
            }
        }

        hashStoreClient.close();
    }
}
