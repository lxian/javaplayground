package com.playground.levelstore.config;

public class Config {

    private String dataDir;

    private int indexDensity;

    private int readParallelism;

    private int maxLevel;

    private long memTableMaxSize;

    private int lvl0CompactThreshold;

    private int compactThreshold;

    private int compactTick;

    public Config(String dataDir, int indexDensity, int readParallelism, int maxLevel, long memTableMaxSize, int lvl0CompactThreshold, int compactThreshold, int compactTick) {
        this.dataDir = dataDir;
        this.indexDensity = indexDensity;
        this.readParallelism = readParallelism;
        this.maxLevel = maxLevel;
        this.memTableMaxSize = memTableMaxSize;
        this.lvl0CompactThreshold = lvl0CompactThreshold;
        this.compactThreshold = compactThreshold;
        this.compactTick = compactTick;
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

    public int getLvl0CompactThreshold() {
        return lvl0CompactThreshold;
    }

    public void setLvl0CompactThreshold(int lvl0CompactThreshold) {
        this.lvl0CompactThreshold = lvl0CompactThreshold;
    }

    public int getCompactThreshold() {
        return compactThreshold;
    }

    public void setCompactThreshold(int compactThreshold) {
        this.compactThreshold = compactThreshold;
    }

    public int getCompactTick() {
        return compactTick;
    }

    public void setCompactTick(int compactTick) {
        this.compactTick = compactTick;
    }
}
