package com.lxian.playground.json.parser.protocol;

public class ByteRange extends AbstractRange<Byte> {

    public ByteRange(int lower) {
        super(Integer.valueOf(lower).byteValue(), null);
    }

    public ByteRange(int lower, int upper) {
        super(Integer.valueOf(lower).byteValue(), Integer.valueOf(upper).byteValue());
    }

    public ByteRange(Byte lower, Byte upper) {
        super(lower, upper);
    }
}
