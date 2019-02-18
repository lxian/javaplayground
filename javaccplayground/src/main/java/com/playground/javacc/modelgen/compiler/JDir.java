package com.playground.javacc.modelgen.compiler;

import java.io.File;

public class JDir {

    File outputDirectory;

    public JDir(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    JFile newFile(String module, String clzName) {
        String fname = module.replace(".", "/");
        if (fname.lastIndexOf("/") == fname.length() - 1) {
            fname += clzName;
        } else {
            fname += "/" + clzName;
        }
        fname += ".java";
        return new JFile(new File(outputDirectory, fname));
    }
}

