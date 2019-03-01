package com.playground.hashstore;

public class ConfigProvider {

    private static Config config;

    public static void setConfig(Config config) {
        ConfigProvider.config = config;
    }

    public static Config config() {
        return null;
    }
}
