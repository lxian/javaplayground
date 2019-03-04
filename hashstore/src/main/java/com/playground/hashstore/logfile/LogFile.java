package com.playground.hashstore.logfile;

import com.playground.hashstore.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class LogFile {

    Logger log = LoggerFactory.getLogger(LogFile.class);

    private long fileIndex;

    private HashMap<String, Long> keyOffsets;

    private File file;

    private FileChannel outCh;

    private ArrayBlockingQueue<FileChannel> inChPool;

    private List<FileChannel> inChs;

    private FileType fileType;

    private LogFile() {
        keyOffsets = new HashMap<String, Long>();
        inChPool = new ArrayBlockingQueue<FileChannel>(ConfigProvider.config().getReadParallelism());
        inChs = new ArrayList<FileChannel>(ConfigProvider.config().getReadParallelism());
    }

    /**
     * create a fresh log file
     * @param file is created and on the path
     */
    public LogFile(long fileIndex, File file, FileType fileType) {
        this();
        this.fileIndex = fileIndex;
        this.file = file;
        this.fileType = fileType;
    }

    public void loadData() throws IOException {
        try (FileChannel inCh = new FileInputStream(file).getChannel()) {
            inCh.position(0);
            ByteBuffer lenBuf = ByteBuffer.allocateDirect(4);
            while (inCh.position() < inCh.size()) {
                long offset = inCh.position();
                lenBuf.clear();
                inCh.read(lenBuf);
                lenBuf.flip();
                ByteBuffer readBuf = ByteBuffer.allocate(lenBuf.getInt());
                inCh.read(readBuf);
                readBuf.flip();
                keyOffsets.put(Entry.read(readBuf).key, offset);
            }
        }
    }

    public void init() {
        enableWrite();
        enableRead();
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
            int inChCnt = ConfigProvider.config().getReadParallelism();
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
        } catch (IOException e) {
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

        keyOffsets.put(key, offset);

        return offset;
    }

    public Entry read(String key) throws IOException {
        Long offset = keyOffsets.get(key);
        if (offset == null) {
            return null;
        }
        FileChannel inCh = inChPool.poll();
        try {
            inCh.position(offset);
            ByteBuffer lenBuf = ByteBuffer.allocate(4);
            inCh.read(lenBuf);
            lenBuf.flip();
            ByteBuffer readBuf = ByteBuffer.allocate(lenBuf.getInt());
            inCh.read(readBuf);
            readBuf.flip();
            return Entry.read(readBuf);
        } finally {
            inChPool.offer(inCh);
        }
    }

    public FileType getFileType() {
        return fileType;
    }

    public File getFile() {
        return file;
    }

    public long getFileIndex() {
        return fileIndex;
    }

    public long size() {
        try {
            return outCh.size();
        } catch (IOException e) {
            log.error("Error getting data file size", e);
            return -1;
        }
    }

    public EntryIterator iterator() throws IOException {
        FileChannel inCh = new FileInputStream(file).getChannel();
        inCh.position(0);
        return new EntryIterator(inCh);
    }


    public class EntryIterator implements Iterator<Entry> {

        private FileChannel inCh;
        private ByteBuffer lenBuf = ByteBuffer.allocateDirect(4);

        public EntryIterator(FileChannel inCh) {
            this.inCh = inCh;
        }

        public boolean hasNext() {
            try {
                return inCh.position() < inCh.size();
            } catch (IOException e) {
                log.error("IO error on in ch while iterating entries", e);
                close();
                return false;
            }
        }

        public Entry next() {
            try {
                lenBuf.clear();
                inCh.read(lenBuf);
                lenBuf.flip();
                ByteBuffer readBuf = ByteBuffer.allocate(lenBuf.getInt());
                inCh.read(readBuf);
                readBuf.flip();
                return Entry.read(readBuf);
            } catch (IOException e) {
                log.error("IO error on in ch while iterating entries", e);
                close();
                return null;
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public void close() {
            try {
                inCh.close();
            } catch (IOException e) {
                log.error("Error closing inCh on entry iteration cleanup", e);
            }
        }
    }
}
