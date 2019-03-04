package com.playground.hashstore.logfile;

public class LogFileNaming {
    public static String getFileName(long fileIdx, FileType type) {
        return fileIdx + suffix(type);
    }

    public static long getFileIdx(String fileName) {
        return Long.valueOf(fileName.substring(0, fileName.indexOf(".")));
    }

    public static FileType getFileTyep(String fileName) {
        return FileType.valueOf(fileName.substring(fileName.indexOf(".")+1).toUpperCase());
    }

    public static boolean isDataFile(String fileName) {
        return fileName.endsWith(suffix(FileType.DATA));
    }

    public static boolean isCompactFile(String fileName) {
        return fileName.endsWith(suffix(FileType.COMPACT));
    }

    static String suffix(FileType fileType) {
        switch (fileType) {
            case COMPACT:
                return ".compact";
            case DATA:
            default:
                return ".data";
        }
    }

}
