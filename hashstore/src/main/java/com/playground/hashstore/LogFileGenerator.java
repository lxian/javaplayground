package com.playground.hashstore;

import com.playground.hashstore.config.ConfigProvider;
import com.playground.hashstore.logfile.FileIndexGenerator;
import com.playground.hashstore.logfile.LogFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class LogFileGenerator {

    private Logger log = LoggerFactory.getLogger(LogFileGenerator.class);


    FileIndexGenerator fileIndexGenerator = new FileIndexGenerator();

    public LogFile createLogFile() {
        long fileIdx = fileIndexGenerator.nextIndex();
        return doCreateLogFile(fileIdx, fileIdx + ".data");
    }

    public LogFile createLogFile(long prevFileIdx) {
        return doCreateLogFile(prevFileIdx, prevFileIdx + ".compact");
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
