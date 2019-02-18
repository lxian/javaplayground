package com.playground.javacc.modelgen.compiler;

public abstract class JAbstrctField implements JField {
    protected String fieldName;

    protected int indent;

    public JAbstrctField(String fieldName) {
        this.fieldName = fieldName;
        this.indent = indent;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    protected JFile withIndent(JFile out) {
        if (indent > 0) {
            StringBuffer sb = new StringBuffer();
            int i = 0;
            while (i != indent) {
                sb.append(" ");
                i++;
            }
            out.write(sb.toString());
        }
        return out;
    }
}
