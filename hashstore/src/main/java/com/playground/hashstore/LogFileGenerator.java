package com.playground.hashstore;

import com.playground.hashstore.config.ConfigProvider;
import com.playground.hashstore.logfile.FileIndexGenerator;
import com.playground.hashstore.logfile.FileType;
import com.playground.hashstore.logfile.LogFile;
import com.playground.hashstore.logfile.LogFileNaming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class LogFileGenerator {

    private Logger log = LoggerFactory.getLogger(LogFileGenerator.class);


    FileIndexGenerator fileIndexGenerator = new FileIndexGenerator();

    public LogFile createLogFile(File dataFile) {
        long fileIdx = LogFileNaming.getFileIdx(dataFile.getName());
        FileType fileType = LogFileNaming.getFileTyep(dataFile.getName());
        return new LogFile(fileIdx, dataFile, fileType);
    }

    public LogFile createLogFile() {
        long fileIdx = fileIndexGenerator.nextIndex();
        return doCreateLogFile(fileIdx, LogFileNaming.getFileName(fileIdx, FileType.DATA), FileType.DATA);
    }

    public LogFile createCompactLogFile(long prevFileIdx) {
        String fileName = LogFileNaming.getFileName(prevFileIdx, FileType.COMPACT);
        return doCreateLogFile(prevFileIdx, fileName, FileType.COMPACT);
    }

    private LogFile doCreateLogFile(long fileIdx, String fileName, FileType fileType) {
        File file = new File(ConfigProvider.config().dataDirFile(), fileName);
        try {
            boolean success = file.createNewFile();
            if (success) {
                return new LogFile(fileIdx, file, fileType);
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
