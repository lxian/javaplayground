package com.playground.hashstore.config;

import java.io.File;

public class Config {

    private int readParallelism;

    private String dataDir;

    private File dataDirFile;

    private int truncateFileSizeThreshold;

    private long compactorTick;

    private int compactFileCountThreshold;

    public int getReadParallelism() {
        return readParallelism;
    }

    public String dataDir() {
        return dataDir;
    }

    public File dataDirFile() {
        return dataDirFile;
    }

    public int truncateFileSizeThreshold() {
        return truncateFileSizeThreshold;
    }

    public long compactorTick() {
        return compactorTick;
    }

    public int compactFileCountThreshold() {
        return compactFileCountThreshold;
//        return compactFileCountThreshold * 1024;
    }

    void setReadParallelism(int readParallelism) {
        this.readParallelism = readParallelism;
    }

    void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    void setDataDirFile(File dataDirFile) {
        this.dataDirFile = dataDirFile;
    }

    void setTruncateFileSizeThreshold(int truncateFileSizeThreshold) {
        this.truncateFileSizeThreshold = truncateFileSizeThreshold;
    }

    void setCompactorTick(long compactorTick) {
        this.compactorTick = compactorTick;
    }

    void setCompactFileCountThreshold(int compactFileCountThreshold) {
        this.compactFileCountThreshold = compactFileCountThreshold;
    }

    @Override
    public String toString() {
        return "Config{" +
                "readParallelism=" + readParallelism +
                ", dataDir='" + dataDir + '\'' +
                ", dataDirFile=" + dataDirFile +
                ", truncateFileSizeThreshold=" + truncateFileSizeThreshold +
                ", compactorTick=" + compactorTick +
                ", compactFileCountThreshold=" + compactFileCountThreshold +
                '}';
    }
}
