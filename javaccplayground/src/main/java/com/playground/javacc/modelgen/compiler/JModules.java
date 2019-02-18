package com.playground.javacc.modelgen.compiler;

import java.util.List;

public class JModules {
    List<JModule> modules;

    public JModules(List<JModule> modules) {
        this.modules = modules;
    }

    public void write(JDir dir) {
        for (JModule module: modules) {
            module.write(dir);
        }
    }

    @Override
    public String toString() {
        return "JModules{" +
                "modules=" + modules +
                '}';
    }
}
