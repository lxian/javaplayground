package com.playground.hashstore;

import com.playground.hashstore.config.ConfigProvider;
import com.playground.hashstore.logfile.Entry;
import com.playground.hashstore.logfile.LogFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogFileCompactor {

    private Logger log = LoggerFactory.getLogger(LogFileCompactor.class);

    private LogFileManager logFileManager;
    private LogFileGenerator logFileGenerator;
    private boolean closing = false;

    public LogFileCompactor(LogFileManager logFileManager, LogFileGenerator logFileGenerator) {
        this.logFileGenerator = logFileGenerator;
        this.logFileManager = logFileManager;
    }

    public void startInBackground() {
        Thread thread = new Thread(this::start);
        thread.setDaemon(true);
        thread.setName("log-file-compactor");
        thread.start();
    }

    public void start() {
        while (!closing) {
            if (logFileManager.currentLogFile().size() > ConfigProvider.config().truncateFileSizeThreshold()) {
                truncateCurrentFile();
            }

            if (logFileManager.oldLogFiles().size() >= ConfigProvider.config().compactFileCountThreshold()) {
                compact(logFileManager.oldLogFiles());
            }

            try {
                Thread.sleep(ConfigProvider.config().compactorTick());
            } catch (InterruptedException e) {
                closing = true;
            }
        }
    }

    public void close() {
        closing = true;
    }

    /**
     * @param toCompactFiles files are sorted from the newest to the oldest
     */
    private void compact(List<LogFile> toCompactFiles) {
        // compact and merge all to-compact logs
        Map<String, Entry> mergedCompactedEntries = new HashMap<String, Entry>();
        for (LogFile toCompact: toCompactFiles) {

            Map<String, Entry> curCompactedEntries = new HashMap<String, Entry>();

            LogFile.EntryIterator entryIterator = null;
            try {
                entryIterator = toCompact.iterator();
            } catch (IOException e) {
                log.error("Error create iterate on logFile {}, skip to the next file", toCompact.getFileIndex(), e);
                continue;
            }

            while (entryIterator.hasNext()) {
                Entry entry = entryIterator.next();
                if (entry != null) {

                    if (mergedCompactedEntries.get(entry.key) != null) { // skip if entry is in the compacted set
                        continue;
                    }

                    curCompactedEntries.put(entry.key, entry); // otherwise put it inside the current file's compacted set
                }
            }
            entryIterator.close();

            mergedCompactedEntries.putAll(curCompactedEntries);
        }


        // write compacted logs to the compated LogFile
        LogFile compacted = logFileGenerator.createCompactLogFile(toCompactFiles.get(0).getFileIndex());
        compacted.init();
        for (Entry entry : mergedCompactedEntries.values()) {
            try {
                compacted.write(entry.key, entry.value);
            } catch (IOException e) {
                log.error("Error writing to compact file log", e);
                compacted.close();
                return;
            }
        }

        // insert the compacted log file and remove the old ones
        logFileManager.add(compacted);
        logFileManager.remove(toCompactFiles);
        for (LogFile logFile: toCompactFiles) {
            logFile.getFile().delete();
        }
    }

    private void truncateCurrentFile() {
        LogFile toTruncate = logFileManager.currentLogFile();
        LogFile logFile = logFileGenerator.createLogFile();
        logFile.init();
        logFileManager.add(logFile);

        compact(Collections.singletonList(toTruncate));
    }

}
