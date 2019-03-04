package com.playground.hashstore;

import com.playground.hashstore.config.ConfigProvider;
import com.playground.hashstore.error.ConfigParseError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HashStoreMain {

    public static void main(String[] args) throws ConfigParseError, IOException {
        for (String arg: args) {
            if (arg.startsWith("conf=")) {
                ConfigProvider.parseConfigFromFile(arg.substring("conf=".length()));
            }
        }

        HashStore hashStore = new HashStore();
        hashStore.start();

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

            String cmd = inputSegs[0].toLowerCase();
            switch (cmd) {
                case "get": {
                    if (inputSegs.length >= 2) {
                        String name = inputSegs[1];
                        byte[] val = hashStore.read(name);
                        System.out.println(val == null ? "NULL" : new String(val));
                    }
                    break;
                }
                case "set": {
                    if (inputSegs.length >= 3) {
                        String name = inputSegs[1];
                        String val = inputSegs[2];
                        hashStore.write(name, val.getBytes());
                        System.out.println();
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
        }

        hashStore.close();
    }

}
