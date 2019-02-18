package com.playground.javacc.modelgen.compiler;

import static com.playground.javacc.modelgen.compiler.Utils.getterName;
import static com.playground.javacc.modelgen.compiler.Utils.setterName;

public class JClazz extends JAbstrctField implements JField {

    private String clz;

    private String clzFullName;

    public JClazz(String clz, String fieldName) {
        super(fieldName);
        int clzStart = clz.lastIndexOf(".");
        if (clzStart != -1) {
            this.clz = clz.substring(clzStart+1);
            this.clzFullName = clz;
        } else {
            this.clz = clz;
            this.clzFullName = null;
        }
    }

    @Override
    public void handleInclusion(JFile out) {
        if (clzFullName != null) {
            withIndent(out).writeln("import " + clzFullName + ";");
        }
    }

    public void writeField(JFile out) {
        withIndent(out).writeln(clz + " " + fieldName + ";\n");
    }

    public void writeGetter(JFile out) {
        withIndent(out).writeln("public " + clz + " " + getterName(fieldName) + "() {");
        withIndent(out).writeln("    return this." + fieldName + ";");
        withIndent(out).writeln("}\n");
    }

    public void writeSetter(JFile out) {
        withIndent(out).writeln("public void " + setterName(fieldName) + "(" + clz + " " + fieldName + ") {");
        withIndent(out).writeln("    this." + fieldName + " = " + fieldName + ";");
        withIndent(out).writeln("}\n");
    }

}
