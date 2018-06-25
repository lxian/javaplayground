package com.lxian.playground.json.parser.object;

import java.util.*;

public class JsObject extends AbstractMap<String, JsValue> implements JsValue {

    private Map<String, JsValue> map;

    public JsObject() {
        this.map = new LinkedHashMap<String, JsValue>();
    }

    public void setValue(JsString key, JsValue value) {
        map.put(key.toJavaString(), value);
    }

    public Set<Entry<String, JsValue>> entrySet() {
        return map.entrySet();
    }

    public Map<String, JsValue> toMap() {
        return map;
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
