package com.playground.hashstore.config;

import java.io.File;

public class DefaultConfig {

    public static Config config() {
        Config config = new Config();
        config.setPort(8900);
        config.setMaxFrameSize(1024);
        config.setCompactFileCountThreshold(2);
        config.setCompactorTick(1000);
        config.setDataDir("./.data");
        config.setReadParallelism(2);
        config.setTruncateFileSizeThreshold(100);

        File dataDir = new File(config.dataDir());
        dataDir.mkdirs();
        config.setDataDirFile(dataDir);
        return config;
    }
}
