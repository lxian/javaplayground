package com.lxian.playground.json.parser.object;

public class JsNull implements JsValue {

    public static final JsNull NULL = new JsNull();

    JsNull() {
    }

    @Override
    public String toString() {
        return "null";
    }
}
