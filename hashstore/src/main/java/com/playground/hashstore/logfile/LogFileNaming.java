package com.playground.hashstore.logfile;

public class LogFileNaming {
    public static String getFileName(long fileIdx) {
        return String.valueOf(fileIdx);
    }

    public static long getFileIdx(String fileName) {
        return Long.valueOf(fileName);
    }

    public static boolean isValidFileName(String fileName) {
        for (int i = 0; i < fileName.length(); i++) {
            if (!(fileName.charAt(0) >= '0' && fileName.charAt(0) <= '9')) {
                return false;
            }
        }
        return true;
    }
}
