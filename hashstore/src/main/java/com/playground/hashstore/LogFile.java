package com.playground.hashstore;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class LogFile {

    private File file;

    private FileChannel outCh;

    private ArrayBlockingQueue<FileChannel> inChPool;

    private List<FileChannel> inChs;

    /**
     * @param file is created and on the path
     */
    public LogFile(File file, Config config) {
        this.file = file;
    }

    public void close() {
        for (FileChannel inCh : inChs) {
            try {
                inCh.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (outCh != null) {
            try {
                outCh.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public synchronized void enableRead() {
        try {
            int inChCnt = ConfigProvider.config().readParallelism;
            while (inChCnt-- > 0) {
                FileChannel inCh = new FileInputStream(file).getChannel();
                inChs.add(inCh);
                inChPool.offer(inCh);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public synchronized void enableWrite() {
        try {
            outCh = new FileOutputStream(file, true).getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * write key-value and return the offset within the file
     * @param key
     * @param value
     * @return
     */
    public long write(String key, byte[] value) throws IOException {
        if (outCh == null) {
            enableWrite();
        }
        Entry entry = new Entry(key, value);
        long offset = outCh.position();
        outCh.write(entry.getWriteBuf());
        return offset;
    }

    ByteBuffer lenBuf = ByteBuffer.allocateDirect(4);

    public Entry read(long offset) throws IOException {
        FileChannel inCh = inChPool.poll();
        try {
            inCh.position(offset);
            inCh.read(lenBuf);
            ByteBuffer readBuf = ByteBuffer.allocate(lenBuf.getInt());
            inCh.read(readBuf);
            return Entry.read(readBuf);
        } finally {
            inChPool.offer(inCh);
        }
    }
}
