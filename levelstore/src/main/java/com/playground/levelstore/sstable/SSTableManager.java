package com.playground.levelstore.sstable;

import com.playground.levelstore.config.ConfigProvider;
import com.playground.levelstore.memtable.MemTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SSTableManager {
    private Logger logger = LoggerFactory.getLogger(SSTableManager.class);
    private SSTableGroup group0;
    private Map<Integer, SSTableGroup> groups;
    private File dataDir;
    private int maxLevel;

    public SSTableManager() {
        String dataDirStr = ConfigProvider.config().getDataDir();
        dataDir = new File(dataDirStr);
        dataDir.mkdirs();

        groups = new HashMap<>();
        maxLevel = ConfigProvider.config().getMaxLevel();
        int level = 0;
        group0 = new SSTableGroup(level, dataDir);
        groups.put(level, group0);
        group0.init();
        level++;

        while (level < maxLevel) {
            SSTableGroup group = new SSTableGroup(level, dataDir);
            groups.put(level, group);
            group.init();
            level++;
        }
    }

    public byte[] get(String key) {
        int level = 0;
        while (level < maxLevel) {
            SSTableGroup ssTableGroup = groups.get(level++);
            if (ssTableGroup == null) {
                return null;
            }
            List<SSTable> ssTables = ssTableGroup.getByRange(key, key);
            for (SSTable ssTable: ssTables) {
                try {
                    Entry entry = ssTable.read(key);
                    if (entry != null) {
                        return entry.value;
                    }
                } catch (IOException e) {
                    logger.error("Error read entry from sstable {}", ssTable.getFile().getAbsolutePath(), e);
                }
            }
        }
        return null;
    }

    public void serilizeMemtable(MemTable memTable) {
        SSTable ssTable = group0.newSSTable();
        ssTable.enableRead();
        ssTable.enableWrite();
        memTable.travelThrough((k, v) -> {
            try {
                ssTable.write(k, v);
            } catch (IOException e) {
                logger.error("Error writting to sstable {}", ssTable.getFile().getAbsolutePath(), e);
            }
        });
        group0.add(ssTable);
    }
}
