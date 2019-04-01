package com.playground.levelstore.config;

public class Config {

    private String dataDir;

    private int indexDensity;

    private int readParallelism;

    private int maxLevel;

    private long memTableMaxSize;

    Config(String dataDir, int indexDensity, int readParallelism, int maxLevel, long memTableMaxSize) {
        this.dataDir = dataDir;
        this.indexDensity = indexDensity;
        this.readParallelism = readParallelism;
        this.maxLevel = maxLevel;
        this.memTableMaxSize = memTableMaxSize;
    }

    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    public int getIndexDensity() {
        return indexDensity;
    }

    public void setIndexDensity(int indexDensity) {
        this.indexDensity = indexDensity;
    }

    public int getReadParallelism() {
        return readParallelism;
    }

    public void setReadParallelism(int readParallelism) {
        this.readParallelism = readParallelism;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public long getMemTableMaxSize() {
        return memTableMaxSize;
    }

    public void setMemTableMaxSize(long memTableMaxSize) {
        this.memTableMaxSize = memTableMaxSize;
    }
}
