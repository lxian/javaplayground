package com.playground.hashstore;

import com.playground.hashstore.config.ConfigProvider;
import com.playground.hashstore.logfile.LogFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class LogFileManager {

    private LogFileGenerator logFileGenerator;
    private List<LogFile> logFiles;

    public LogFileManager(LogFileGenerator logFileGenerator) {
        this.logFileGenerator = logFileGenerator;
        this.logFiles = new LinkedList<>();
    }

    /**
     * rebuild log file list from disk
     */
    public void loadData() throws IOException {
        File dataDir = ConfigProvider.config().dataDirFile();
        File[] dataFiles = dataDir.listFiles();
        if (dataFiles == null || dataFiles.length == 0) {
            LogFile logFile = logFileGenerator.createLogFile();
            logFile.init();
            add(logFile);
        } else {
            for (File dataFile: dataFiles) {
                String fileName = dataFile.getName();
                LogFile logFile = new LogFile(Long.valueOf(fileName.substring(0, fileName.indexOf("."))), dataFile);
                logFile.loadData();
                logFile.init();
                add(logFile);
            }
        }
    }

    public LogFile currentLogFile() {
        return logFiles.get(0);
    }

    public List<LogFile> oldLogFiles() {
        return logFiles.subList(1, logFiles.size());
    }

    public List<LogFile> logFiles() {
        return Collections.unmodifiableList(logFiles);
    }

    // only to be invoked by the compactor thread
    public void add(LogFile logFile) {
        List<LogFile> newLogFiles = new LinkedList<LogFile>(logFiles);
        int i = 0;
        for (; i < newLogFiles.size(); i++) {
            if (newLogFiles.get(i).getFileIndex() < logFile.getFileIndex()) {
                newLogFiles.add(i, logFile);
                break;
            }
        }
        if (i == newLogFiles.size()) {
            newLogFiles.add(logFile);
        }

        logFiles = newLogFiles;
    }

    // only to be invoked by the compactor thread
    public void remove(Collection<LogFile> toRemove) {
        List<LogFile> newLogFiles = new LinkedList<LogFile>(logFiles);
        newLogFiles.removeAll(toRemove);

        for (LogFile logFile: toRemove) {
            logFile.close();
        }

        logFiles = newLogFiles;
    }

    public void close() {
        for (LogFile logFile: logFiles) {
            logFile.close();
        }
    }
}
