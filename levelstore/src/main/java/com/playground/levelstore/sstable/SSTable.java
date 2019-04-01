package com.playground.levelstore.sstable;

import com.playground.levelstore.config.ConfigProvider;
import com.playground.levelstore.avltree.AvlTree;
import com.playground.levelstore.avltree.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class SSTable {

    Logger log = LoggerFactory.getLogger(SSTable.class);

    private Entry firstEntry;

    private Entry lastEntry;

    private IndexDensity indexDensity;

    private AvlTree<String> keyOffsets;

    private File file;

    private FileChannel outCh;

    private ArrayBlockingQueue<FileChannel> inChPool;

    private List<FileChannel> inChs;

    private SSTable() {
        keyOffsets = new AvlTree<>();
        inChPool = new ArrayBlockingQueue<FileChannel>(ConfigProvider.config().getReadParallelism());
        inChs = new ArrayList<FileChannel>(ConfigProvider.config().getReadParallelism());
    }

    /**
     * create a fresh log file
     * @param file is created and on the path
     */
    public SSTable(File file, IndexDensity indexDensity) {
        this();
        this.file = file;
        this.indexDensity = indexDensity;
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
                lastEntry = Entry.read(readBuf);
                if (firstEntry == null) {
                    firstEntry = lastEntry;
                }
                if (indexDensity.shouldAddIndex()) {
                    addIndex(lastEntry.key, offset);
                }
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

    public void addIndex(String key, long offset) throws IOException {
        keyOffsets.insert(key, offset);
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

        if (indexDensity.shouldAddIndex()) {
            addIndex(key, offset);
        }

        lastEntry = entry;
        if (firstEntry == null) {
            firstEntry = entry;
        }

        return offset;
    }

    public Entry read(String key) throws IOException {
        Node<String> offset = keyOffsets.findNearest(key);
        if (offset == null) {
            return null;
        }
        FileChannel inCh = inChPool.poll();
        try {
            EntryIterator entryIterator = iterator(inCh, (Long) offset.getValue());
            while (entryIterator.hasNext()) {
                Entry entry = entryIterator.next();
                if (entry.key.equals(key)) {
                    return entry;
                } else if (entry.key.compareTo(key) > 0) {
                    return null;
                }
            }
        } finally {
            inChPool.offer(inCh);
        }
        return null;
    }

    public File getFile() {
        return file;
    }

    public long size() {
        try {
            return outCh.size();
        } catch (IOException e) {
            log.error("Error getting data file size", e);
            return -1;
        }
    }

    public EntryIterator iterator(long startPosition) throws IOException {
        FileChannel inCh = new FileInputStream(file).getChannel();
        inCh.position(startPosition);
        return new EntryIterator(inCh);
    }

    public EntryIterator iterator(FileChannel inCh, long startPosition) throws IOException {
        inCh.position(startPosition);
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

    public IndexDensity getIndexDensity() {
        return indexDensity;
    }

    public void setIndexDensity(IndexDensity indexDensity) {
        this.indexDensity = indexDensity;
    }

    public Entry getFirstEntry() {
        return firstEntry;
    }

    public void setFirstEntry(Entry firstEntry) {
        this.firstEntry = firstEntry;
    }

    public Entry getLastEntry() {
        return lastEntry;
    }

    public void setLastEntry(Entry lastEntry) {
        this.lastEntry = lastEntry;
    }
}
