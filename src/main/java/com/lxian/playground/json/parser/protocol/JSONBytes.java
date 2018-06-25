package com.lxian.playground.json.parser.protocol;

public class JSONBytes {

    public static final byte SPACE = 0x20;

    public static final byte TAB = 0x09;

    public static final byte NEW_LINE = 0x0A;

    public static final byte RETURN = 0x0D;

    public static final byte SOLIDUS = 0x2F;

    public static final byte REV_SOLIDUS = 0x5C;

    public static final byte BACK_SPACE = 0x62;

    public static final byte FORM_FEED = 0x66;

    public static final byte LINE_FEED = 0x6E;

    public static final byte uXXXX = 0x75;

    public static final AbstractRange.MultiRange<Byte> STRING_UNESCAPE_RANGES =
            new ByteRange(0x20, 0x21).or(new ByteRange(0x23, 0x5B)).or(new ByteRange(0x5D));

    public static final byte[] NULL = {'n', 'u', 'l', 'l'};

    public static final byte[] FALSE = {'f', 'a', 'l', 's', 'e'};

    public static final byte[] TRUE = {'t', 'r', 'u', 'e'};

}
