package com.playground.hashstore;

import com.playground.hashstore.logfile.Entry;
import com.playground.hashstore.logfile.LogFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HashStore {

    private Logger log = LoggerFactory.getLogger(HashStore.class);

    private final LogFileGenerator logFileGenerator;
    private LogFileManager logFileManager;
    private LogFileCompactor logFileCompactor;

    public HashStore() {
        logFileGenerator = new LogFileGenerator();
        // restore data from disk
        logFileManager = new LogFileManager(logFileGenerator);

        // start compactor in the background
        logFileCompactor = new LogFileCompactor(logFileManager, logFileGenerator);
    }

    public void start() {
        try {
            logFileManager.loadData();
        } catch (IOException e) {
            log.error("Failed to load log file data", e);
            throw new RuntimeException("Log file data load failed");
        }
        logFileCompactor.startInBackground();
    }

    public void close() {
        logFileCompactor.close();
        logFileManager.close();
    }

    public void write(String key, byte[] value) throws IOException {
        logFileManager.currentLogFile().write(key, value);
    }

    public byte[] read(String key) throws IOException {
        for (LogFile logFile: logFileManager.logFiles()) {
            Entry entry;
            if ((entry = logFile.read(key)) != null) {
                return entry.value;
            }
        }
        return null;
    }
}
