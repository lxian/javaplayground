package com.playground.hashstore;

import com.playground.hashstore.config.ConfigProvider;
import com.playground.hashstore.error.ConfigParseError;
import com.playground.hashstore.server.HashStoreServer;

public class HashStoreServerMain {

    public static void main(String[] args) throws ConfigParseError {
        for (String arg: args) {
            if (arg.startsWith("conf=")) {
                ConfigProvider.parseConfigFromFile(arg.substring("conf=".length()));
            }
        }

        new HashStoreServer(new HashStore()).start();
    }
}
