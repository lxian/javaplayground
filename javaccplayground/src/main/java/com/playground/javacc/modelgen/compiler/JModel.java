package com.playground.javacc.modelgen.compiler;

import java.util.List;

public class JModel {
    String packageName;
    String clzName;
    List<JField> fields;

    public JModel(String clzName, List<JField> fields) {
        this.clzName = clzName;
        this.fields = fields;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void write(JFile out) {
        out.writeln("package " + packageName + ";");
        out.writeln();

        for (JField field : fields) {
            field.handleInclusion(out);
        }

        out.writeln();
        out.writeln("public class " + clzName + " {");

        for (JField field : fields) {
            field.setIndent(4);
            field.writeField(out);
        }
        for (JField field : fields) {
            field.setIndent(4);
            field.writeGetter(out);
            field.writeSetter(out);
        }

        out.writeln("}");
        out.flush();
    }

    @Override
    public String toString() {
        return "JModel{" +
                "packageName='" + packageName +
                ", clzName='" + clzName +
                ", fields=" + fields +
                '}';
    }
}
