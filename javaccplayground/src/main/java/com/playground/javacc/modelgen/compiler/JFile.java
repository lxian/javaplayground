package com.playground.javacc.modelgen.compiler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JFile {

    File file;
    FileWriter fileWriter;
    StringBuffer sb;

    public JFile(File file) {
        this.file = file;
        sb = new StringBuffer();

        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("Error create file " + file.getName());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void write(String str) {
        sb.append(str);
    }

    public void writeln() {
        write("\n");
    }

    public void writeln(String str) {
        write(str + "\n");
    }

    public void flush() {

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(sb.toString());
            fileWriter.flush();
        } catch (IOException e) {
            System.out.println("Failed write to file " + file.getName());
            e.printStackTrace();
        }
    }
}

