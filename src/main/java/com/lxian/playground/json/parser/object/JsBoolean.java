package com.lxian.playground.json.parser.object;

public class JsBoolean implements JsValue {

    public static final JsBoolean TRUE = new JsBoolean(true);

    public static final JsBoolean FALSE = new JsBoolean(false);

    private boolean bool;

    private JsBoolean(boolean bool) {
        this.bool = bool;
    }

    public boolean toBool() {
        return bool;
    }

    @Override
    public String toString() {
        return bool ? "true" : "false";
    }
}
