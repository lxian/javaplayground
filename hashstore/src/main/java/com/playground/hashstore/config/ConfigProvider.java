package com.playground.hashstore.config;

import com.playground.hashstore.error.ConfigParseError;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigProvider {

    private static Config config;

    public static void setConfig(Config config) {
        ConfigProvider.config = config;
    }

    public static void parseConfigFromFile(String path) throws ConfigParseError {
        File file = new File(path);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new ConfigParseError("Config file: " + file.getAbsolutePath() + " is not found.", e);
        }
        Properties prop = new Properties();
        try {
            prop.load(fis);
        } catch (IOException e) {
            throw new ConfigParseError("Fails to read config file: " + path, e);
        }

        Config config = new Config();
        config.setPort(Integer.valueOf(prop.getProperty("server.port")));
        config.setMaxFrameSize(Integer.valueOf(prop.getProperty("server.max-frame-size")));
        config.setCompactFileCountThreshold(Integer.valueOf(prop.getProperty("compactor.compact.file-count-threshold")));
        config.setCompactorTick(Long.valueOf(prop.getProperty("compactor.tick")));
        config.setDataDir(prop.getProperty("data-dir"));
        config.setReadParallelism(Integer.valueOf(prop.getProperty("read.parallelism")));
        config.setTruncateFileSizeThreshold(Integer.valueOf(prop.getProperty("compactor.truncate.file-size-threshold")));

        File dataDir = new File(config.dataDir());
        dataDir.mkdirs();
        config.setDataDirFile(dataDir);
        setConfig(config);
    }

    public static Config config() {
        if (config == null) {
            throw new RuntimeException("config is missing");
        }
        return config;
    }
}
