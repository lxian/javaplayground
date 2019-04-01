package com.playground.levelstore;

import com.playground.levelstore.config.Config;
import com.playground.levelstore.config.ConfigProvider;
import com.playground.levelstore.memtable.AvlMemTable;
import com.playground.levelstore.memtable.MemTable;
import com.playground.levelstore.sstable.Compactor;
import com.playground.levelstore.sstable.SSTableManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LevelStore {

    private Config config;
    private MemTable memTable;
    private volatile MemTable immutableMemTable;
    private SSTableManager ssTableManager;
    private Compactor compactor;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();


    public LevelStore(Config config) {
        this.config = config;
        ConfigProvider.setConfig(config);

        this.memTable = new AvlMemTable();
        this.ssTableManager = new SSTableManager();

        this.compactor = new Compactor(ssTableManager);
        this.compactor.startBackgroundCompact();
    }

    void put(String key, byte[] value) {
        memTable.put(key, value);
        if (memTable.size() > ConfigProvider.config().getMemTableMaxSize() && immutableMemTable == null) {
            memTable.markImmutable();
            immutableMemTable = memTable;
            executorService.submit(() -> {
                compactor.compactMemtable(immutableMemTable);
                immutableMemTable = null;
            });
            memTable = new AvlMemTable();
        }
    }

    byte[] get(String key) {
        byte[] val = memTable.get(key);
        if (val != null) {
            return val;
        }
        MemTable immutableMemTable = this.immutableMemTable;
        if (immutableMemTable != null) {
            val = memTable.get(key);
            if (val != null) {
                return val;
            }
        }
        return ssTableManager.get(key);
    }

    // for testing only
    public void flushAllToDisk() {
        if (memTable.size() > 0) {
            while (true) {
                if (immutableMemTable == null) {
                    compactor.compactMemtable(memTable);
                    return;
                }
            }
        }
    }
}
