package com.playground.levelstore.sstable;

import com.playground.levelstore.memtable.MemTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Compactor {

    private Logger logger = LoggerFactory.getLogger(Compactor.class);
    private SSTableManager ssTableManager;
    private ExecutorService executorService;

    public Compactor(SSTableManager ssTableManager) {
        this.ssTableManager = ssTableManager;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void compactMemtable(MemTable memTable) {
        executorService.submit(() -> {
            try {
                ssTableManager.serilizeMemtable(memTable);
            } catch (Throwable e) {
                logger.error("Error serlizing memtable", e);
            }
            return true;
        });
    }

    public void startBackgroundCompact() {
    }

    private void compact() {
    }
}
