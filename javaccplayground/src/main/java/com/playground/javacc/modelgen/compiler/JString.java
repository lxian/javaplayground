package com.playground.javacc.modelgen.compiler;

import static com.playground.javacc.modelgen.compiler.Utils.*;

public class JString extends JAbstrctField implements JField {

    public JString(String fieldName) {
        super(fieldName);
    }

    public void writeField(JFile out) {
        withIndent(out).writeln("String " + fieldName + ";\n");
    }

    public void writeGetter(JFile out) {
        withIndent(out).writeln("public String " + getterName(fieldName) + "() {");
        withIndent(out).writeln("    return this." + fieldName + ";");
        withIndent(out).writeln("}\n");
    }

    public void writeSetter(JFile out) {
        withIndent(out).writeln("public void " + setterName(fieldName) + "(String " + fieldName + ") {");
        withIndent(out).writeln("    this." + fieldName + " = " + fieldName + ";");
        withIndent(out).writeln("}\n");
    }

    @Override
    public String toString() {
        return "JString{" +
                "fieldName='" + fieldName +
                ", indent=" + indent +
                '}';
    }
}
