package com.playground.hashstore.logfile;

public class FileIndexGenerator {

    private long counter = 0;

    private long counter() {
        if (counter > 65535) { // 2 ^ 16
            counter = 0;
        }
        return counter++;
    }

    public long nextIndex() {
        return (System.currentTimeMillis() << 16) | counter();
    }
}
