package com.lxian.playground.json.parser.object;

import java.math.BigDecimal;

public class JsNumber implements JsValue {

    private boolean positive = true;

    private String sign = "+";

    private byte[] integer;

    private byte[] fraction;

    private byte[] exp;

    public JsNumber() {
    }

    public void setPositive(boolean positive) {
        this.positive = positive;
        this.sign = positive ? "+": "-";
    }

    public void setInteger(byte[] integer) {
        this.integer = integer;
    }

    public void setFraction(byte[] fraction) {
        this.fraction = fraction;
    }

    public void setExp(byte[] exp) {
        this.exp = exp;
    }

    @Override
    public String toString() {
        String str = sign;
        if (integer != null) {
            str += new String(integer);
        }
        if (fraction != null) {
            str += new String(fraction);
        }
        if (exp != null) {
            str += new String(exp);
        }
        return str;
    }

    private String stringify(byte[] bytes) {
        return bytes == null ? "" : new String(bytes);
    }

    public Short toShort() {
        return Short.valueOf(this.sign + stringify(integer));
    }

    public Integer toInteger() {
        return Integer.valueOf(this.sign + stringify(integer));
    }

    public Long toLong() {
        return Long.valueOf(this.sign + stringify(integer) + "L");
    }

    public Float toFloat() {
        return Float.valueOf(this.sign + stringify(integer) + stringify(fraction) + stringify(exp));
    }

    public Double toDouble() {
        return Double.valueOf(this.sign + stringify(integer) + stringify(fraction) + stringify(exp));
    }

    public BigDecimal toBigDecimal() {
        return new BigDecimal(this.sign + stringify(integer) + stringify(fraction) + stringify(exp));
    }
}
