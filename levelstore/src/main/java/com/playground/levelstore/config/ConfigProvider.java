package com.playground.levelstore.config;

public class ConfigProvider {

    private static Config config;

    public static void setConfig(Config config) {
        ConfigProvider.config = config;
    }

    public static Config config() {
        if (config == null) {
            throw new RuntimeException("config is missing");
        }
        return config;
    }
}
