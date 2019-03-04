package com.playground.hashstore;

import com.playground.hashstore.config.ConfigProvider;
import com.playground.hashstore.logfile.FileType;
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
            LogFile logFile = logFileGenerator.createLogFile(dataFile);
            allLogFiles.add(logFile);
        }
        allLogFiles.sort((l1, l2) -> (int)(l2.getFileIndex() - l1.getFileIndex()));

        // resolve inconsistency
        List<LogFile> resolved = new LinkedList<LogFile>();
        for (int i = 0; i < allLogFiles.size(); i++) {
            LogFile logFile = allLogFiles.get(i);
            if (logFile.getFileType() == FileType.DATA || i + 1 == allLogFiles.size()) { // add Data files
                resolved.add(logFile);
                continue;
            }

            // for compact files
            // check if the next file is Data file
            // if so, try to resolve the inconsistency
            //
            // case1: 123.compact 123.data 122.data 121.compact => remove 123.compact
            // case2: 123.compact 122.data 121.compact => remove 122.data
            LogFile nextLogFile = allLogFiles.get(i+1);
            if (nextLogFile.getFileType() == FileType.DATA) {
                if (nextLogFile.getFileIndex() == logFile.getFileIndex()) {
                    // remove the COMPACT file
                    logFile.getFile().delete();
                } else {
                    // remove Data files until the next COMPACT file
                    resolved.add(logFile);
                    int j = i+1;
                    for (; j < allLogFiles.size(); j++) {
                        if (logFile.getFileType() == FileType.DATA) {
                            logFile.getFile().delete();
                        } else {
                            break;
                        }
                    }
                    i = j;
                }
            }
        }

        // create new logFile on need
        boolean needNewLogFile = true;
        for (File file : dataFiles) {
            if (LogFileNaming.isDataFile(file.getName())) {
                needNewLogFile = false;
                break;
            }
        }
        if (needNewLogFile) {
            LogFile logFile = logFileGenerator.createLogFile();
            resolved.add(0, logFile);
        }

        // load and init log files
        this.logFiles = resolved;
        for (LogFile logFile: this.logFiles) {
            logFile.loadData();
            logFile.init();
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
