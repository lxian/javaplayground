package com.playground.javacc.modelgen.compiler;

public interface JField {
    default void handleInclusion(JFile out) {
    }
    void writeField(JFile out);
    void writeGetter(JFile out);
    void writeSetter(JFile out);
    void setIndent(int indent);
}
