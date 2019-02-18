package com.playground.javacc.modelgen.compiler;

import static com.playground.javacc.modelgen.compiler.Utils.getterName;
import static com.playground.javacc.modelgen.compiler.Utils.setterName;

public class JInt extends JAbstrctField implements JField {

    public JInt(String fieldName) {
        super(fieldName);
    }

    public void writeField(JFile out) {
        withIndent(out).writeln("int " + fieldName + ";\n");
    }

    public void writeGetter(JFile out) {
        withIndent(out).writeln("public int " + getterName(fieldName) + "() {");
        withIndent(out).writeln("    return this." + fieldName + ";");
        withIndent(out).writeln("}\n");
    }

    public void writeSetter(JFile out) {
        withIndent(out).writeln("public void " + setterName(fieldName) + "(int " + fieldName + ") {");
        withIndent(out).writeln("    this." + fieldName + " = " + fieldName + ";");
        withIndent(out).writeln("}\n");
    }

    @Override
    public String toString() {
        return "JInt{" +
                "fieldName='" + fieldName +
                ", indent=" + indent +
                '}';
    }
}
