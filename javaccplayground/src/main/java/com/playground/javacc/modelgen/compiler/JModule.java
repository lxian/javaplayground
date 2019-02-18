package com.playground.javacc.modelgen.compiler;

import java.util.List;

public class JModule {
    private String name;

    private List<JModel> models;

    public JModule(String name, List<JModel> models) {
        this.name = name;
        this.models = models;
    }

    public void write(JDir jDir) {
        for (JModel model: models) {
            model.setPackageName(name);
            model.write(jDir.newFile(name, model.clzName));
        }
    }

    @Override
    public String toString() {
        return "JModule{" +
                "name='" + name +
                ", models=" + models +
                '}';
    }
}
