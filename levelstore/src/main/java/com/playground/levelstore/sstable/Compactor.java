package com.playground.levelstore.sstable;

import com.playground.levelstore.config.ConfigProvider;
import com.playground.levelstore.config.Constants;
import com.playground.levelstore.memtable.MemTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Compactor {

    private Logger logger = LoggerFactory.getLogger(Compactor.class);
    private SSTableManager ssTableManager;
    private ScheduledExecutorService scheduledExecutorService;

    public Compactor(SSTableManager ssTableManager) {
        this.ssTableManager = ssTableManager;
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void compactMemtable(MemTable memTable, Consumer<Boolean> onComplete) {
        boolean success = false;
        try {
            ssTableManager.serilizeMemtable(memTable);
            success = true;
        } catch (Throwable e) {
            logger.error("Error serlizing memtable", e);
        }
        onComplete.accept(success);
    }

    public void startBackgroundCompact() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                this.compact();
            } catch (Throwable e) {
                logger.error("Error compacting sstables", e);
            }
        }, 1000, ConfigProvider.config().getCompactTick(), TimeUnit.MILLISECONDS);
    }

    private void compact() {
        // check lvl 0
        SSTableGroup group = ssTableManager.getGroup(0);
        if (group.tableCount() > ConfigProvider.config().getLvl0CompactThreshold()) {
            SSTable table = group.getTable(Constants.random.nextInt(group.tableCount()));
            try {
                doCompact0(table.getLastEntry().key, table.getLastEntry().key);
            } catch (IOException e) {
                logger.error("Error compacting sstables at level 0", e);
            }
        }

        for (int level = 1; level < ssTableManager.getMaxLevel() - 1; level++) {
            List<SSTable> ssTables = ssTableManager.getGroup(level).listTables();
            for (SSTable ssTable: ssTables) {
                if (ssTable.size() > ConfigProvider.config().getCompactThreshold()) {
                    try {
                        doCompact(ssTable, level+1);
                        break;
                    } catch (IOException e) {
                        logger.error("Error compacting sstables at level " + level, e);
                    }
                }
            }
        }
    }

    private void doCompact(SSTable ssTable, int level) throws IOException {
        List<SSTable> tables = ssTableManager.getGroup(level).getByRange(ssTable.getFirstEntry().key, ssTable.getLastEntry().key);
        tables.add(0, ssTable);
        doMerge(tables, level);

        tables.forEach(t -> ssTableManager.getGroup(level).remove(t));
        ssTableManager.getGroup(0).remove(ssTable);
        ssTable.getFile().delete();
    }

    private void doCompact0(String startKey, String endKey) throws IOException {
        List<SSTable> tables0 = ssTableManager.getGroup(0).getByRange(startKey, endKey);
        List<SSTable> tables1 = ssTableManager.getGroup(1).getByRange(startKey, endKey);
        List<SSTable> tables = new LinkedList<>();
        tables.addAll(tables0);
        tables.addAll(tables1);

        doMerge(tables, 1);

        tables0.forEach(t -> ssTableManager.getGroup(0).remove(t));
        tables1.forEach(t -> ssTableManager.getGroup(1).remove(t));
        tables0.forEach(t -> t.getFile().delete());
        tables1.forEach(t -> t.getFile().delete());
    }

    private void doMerge(List<SSTable> tables, int targetLevel) throws IOException {
        List<Entry> data = new LinkedList<>();
        SSTable table0 = tables.get(0);
        SSTable.EntryIterator iterator0 = table0.iterator(0);
        iterator0.forEachRemaining(data::add);

        for (int idx = 1; idx < tables.size(); idx++) {
            SSTable table = tables.get(idx);
            SSTable.EntryIterator iterator = table.iterator(0);
            int i = 0;
            while (iterator.hasNext() && i < data.size()) {
                Entry e = iterator.next();
                while (i < data.size() && data.get(i).key.compareTo(e.key) < 0) {
                    i++;
                }
                if (i < data.size() && data.get(i).key.compareTo(e.key) > 0) {
                    data.add(i, e);
                }
                i++;
            }
            iterator.forEachRemaining(data::add);
        }

        ssTableManager.newSSTable(data, targetLevel);
    }
}
