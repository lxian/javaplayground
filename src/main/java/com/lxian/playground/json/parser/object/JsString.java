package com.lxian.playground.json.parser.object;

import java.io.ByteArrayOutputStream;

public class JsString implements JsValue {

    private ByteArrayOutputStream byteArrayOutputStream;

    public JsString() {
        byteArrayOutputStream = new ByteArrayOutputStream();
    }

    public void append(byte b) {
        byteArrayOutputStream.write(b);
    }

    public String toString() {
        return "\"" + byteArrayOutputStream.toString() + "\"";
    }

    public String toJavaString() {
        return byteArrayOutputStream.toString();
    }

}
