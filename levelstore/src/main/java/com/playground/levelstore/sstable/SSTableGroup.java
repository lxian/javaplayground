package com.playground.levelstore.sstable;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SSTableGroup {

    private int level;
    private List<SSTable> ssTables;
    private File directory;
    private SSTableLoader ssTableLoader;

    public SSTableGroup(int level, File baseDir) {
        this.level = level;
        this.directory = new File(baseDir, String.valueOf(getLevel()));
        this.directory.mkdirs();
        this.ssTableLoader = new SSTableLoader(this.directory);
    }

    public void init() {
        ssTables = ssTableLoader.load();
        for (SSTable ssTable: ssTables) {
            ssTable.init();
        }
    }

    public int getLevel() {
        return level;
    }

    public List<SSTable> getByRange(String start, String end) {
        List<SSTable> inRange = new LinkedList<>();
        for (SSTable table : ssTables) {
            if (table.getFirstEntry().key.compareTo(start) <= 0 &&
                    table.getLastEntry().key.compareTo(end) >= 0) {
                inRange.add(table);
            }
        }
        return inRange;
    }

    public void remove(SSTable ssTable) {
        if (ssTables.remove(ssTable)) {
            ssTable.close();
        }
    }

    public void add(SSTable ssTable) {
        ssTables.add(ssTable);
    }

    public void add(int pos, SSTable ssTable) {
        ssTables.add(pos, ssTable);
    }

    public SSTable newSSTable() {
        return ssTableLoader.newSSTable();
    }

    int tableCount() {
        return ssTables.size();
    }

    List<SSTable> listTables() {
        return Collections.unmodifiableList(ssTables);
    }

    SSTable getTable(int idx) {
        return ssTables.get(idx);
    }
}
