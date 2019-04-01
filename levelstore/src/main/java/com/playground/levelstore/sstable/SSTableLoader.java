package com.playground.levelstore.sstable;

import com.playground.levelstore.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SSTableLoader {

    private Logger logger = LoggerFactory.getLogger(SSTableLoader.class);

    private File dir;

    public SSTableLoader(File directory) {
        this.dir = directory;
        dir.mkdirs();
    }

    public List<SSTable> load() {
        List<SSTable> loaded = new LinkedList<>();
        File[] files = dir.listFiles();
        for (File file: files) {
            if (file.isDirectory()) {
                continue;
            }
            if (!file.getName().endsWith(Constants.SS_TABLE_FILE_POSTFIX)) {
                continue;
            }
            SSTable ssTable = load(file);
            if (ssTable != null) {
                loaded.add(ssTable);
            }
        }
        return loaded;
    }

    public SSTable load(File file) {
        SSTable ssTable = new SSTable(file, new IndexDensity());
        try {
            ssTable.loadData();
            return ssTable;
        } catch (IOException e) {
            logger.error("Error loading sstable data {}", file.getAbsoluteFile(), e);
            return null;
        }
    }

    public SSTable newSSTable() {
        String fileName = String.valueOf(System.currentTimeMillis()) + Constants.SS_TABLE_FILE_POSTFIX;
        File file = new File(dir, fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new SSTable(file, new IndexDensity());
    }
}
