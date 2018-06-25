package com.lxian.playground.json.parser.object;

import java.util.LinkedList;
import java.util.List;

public class JsList implements JsValue {

    List<JsValue> list = new LinkedList<JsValue>();

    public void apped(JsValue value) {
        list.add(value);
    }

    public List<JsValue> toList() {
        return list;
    }

    public JsValue[] toArray() {
        return list.toArray(new JsValue[list.size()]);
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
