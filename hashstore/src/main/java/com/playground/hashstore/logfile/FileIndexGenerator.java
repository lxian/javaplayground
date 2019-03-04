package com.playground.hashstore.logfile;

public class FileIndexGenerator {

    private long counter = 0;

    private long counter() {
        if (counter > 65535) { // 2 ^ 16
            counter = 0;
        }
        return counter+=10; // leave some slot for the compact
    }

    public long nextIndex() {
        return (System.currentTimeMillis() << 16) | counter();
    }

    /**
     * @param prevIndex
     * @return a index that is just bigger than the prevIndex
     */
    public long nextIndex(long prevIndex) {
        return prevIndex + 1;
    }
}
