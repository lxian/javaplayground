package com.playground.hashstore;

import com.playground.hashstore.config.ConfigProvider;
import com.playground.hashstore.logfile.LogFile;
import com.playground.hashstore.logfile.LogFileNaming;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
        dataFiles = dataFiles == null ? new File[0] : dataFiles;

        // create log files from disk
        List<LogFile> allLogFiles = new LinkedList<LogFile>();
        for (File dataFile : dataFiles) {
            if (LogFileNaming.isValidFileName(dataFile.getName())) {
                LogFile logFile = logFileGenerator.createLogFile(dataFile);
                allLogFiles.add(logFile);
            }
        }
        if (allLogFiles.size() == 0) {
            LogFile freshLogFile = logFileGenerator.createLogFile();
            allLogFiles.add(freshLogFile);
        }
        allLogFiles.sort((l1, l2) -> (int)(l2.getFileIndex() - l1.getFileIndex()));

        // load and init log files
        for (LogFile logFile: allLogFiles) {
            logFile.loadData();
            logFile.init();
        }
        this.logFiles = allLogFiles;
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
