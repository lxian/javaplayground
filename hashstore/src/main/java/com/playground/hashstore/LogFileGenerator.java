package com.playground.hashstore;

import com.playground.hashstore.config.ConfigProvider;
import com.playground.hashstore.logfile.FileIndexGenerator;
import com.playground.hashstore.logfile.LogFile;
import com.playground.hashstore.logfile.LogFileNaming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class LogFileGenerator {

    private Logger log = LoggerFactory.getLogger(LogFileGenerator.class);

    private final FileIndexGenerator fileIndexGenerator = new FileIndexGenerator();

    public LogFile createLogFile(File dataFile) {
        long fileIdx = LogFileNaming.getFileIdx(dataFile.getName());
        return new LogFile(fileIdx, dataFile);
    }

    public LogFile createLogFile() {
        long fileIdx = fileIndexGenerator.nextIndex();
        return doCreateLogFile(fileIdx, LogFileNaming.getFileName(fileIdx));
    }

    public LogFile createCompactLogFile(long prevFileIdx) {
        long fileIdx = fileIndexGenerator.nextIndex(prevFileIdx);
        String fileName = LogFileNaming.getFileName(fileIdx);
        return doCreateLogFile(fileIdx, fileName);
    }

    private LogFile doCreateLogFile(long fileIdx, String fileName) {
        File file = new File(ConfigProvider.config().dataDirFile(), fileName);
        try {
            boolean success = file.createNewFile();
            if (success) {
                return new LogFile(fileIdx, file);
            } else {
                log.error("Create Log File {} failed. File exists already", file.getAbsoluteFile());
                return null;
            }
        } catch (IOException e) {
            log.error("Create Log File {} failed", file.getAbsoluteFile(), e);
            return null;
        }
    }
}
