package com.lxian.playground.json.parser;

import com.lxian.playground.json.parser.error.InvalidJsonError;
import com.lxian.playground.json.parser.object.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.lxian.playground.json.parser.protocol.JSONBytes.*;

public class DefaultJsonParser extends AbstractJsonParser {

    private InputStream in;

    private ByteArrayOutputStream cache = new ByteArrayOutputStream();

    private byte currentByte;

    private int readCount;

    public JsObject parse(InputStream in) throws IOException, InvalidJsonError {
        this.in = in;
        read();
        JsObject jsObject = parseObject();
        reset();
        return jsObject;
    }

    private void reset() {
        readCount = 0;
        currentByte = 0;
        cache.reset();
        in = null;
    }

    private JsValue parseValue() throws IOException, InvalidJsonError {
        skipWhiteSpaces();
        switch (currentByte) {
            case 't': return parseTrue();
            case 'f': return parseFalse();
            case '"': return parseString();
            case 'n': return parseNull();
            case '{': return parseObject();
            case '[': return parseList();
            default: return parseNumber();
        }
    }

    private JsObject parseObject() throws IOException, InvalidJsonError {
        skipWhiteSpaces();
        if (currentByte != '{') {
            throw createInvalidJsonError();
        }
        read();

        JsObject jsObject = new JsObject();

        skipWhiteSpaces();
        if (currentByte == '}') {
            read();
            return jsObject;
        }

        while (true) {
            JsString key = parseString();

            skipWhiteSpaces();
            if (currentByte != ':') {
                throw createInvalidJsonError();
            }
            read();

            JsValue value = parseValue();

            jsObject.setValue(key, value);

            skipWhiteSpaces();
            switch (currentByte) {
                case ',': {
                    read();
                    continue;
                }
                case '}': {
                    read();
                    return jsObject;
                }
                default: throw createInvalidJsonError();
            }
        }

    }

    private JsList parseList() throws InvalidJsonError, IOException {
        skipWhiteSpaces();
        if (currentByte != '[') {
            throw createInvalidJsonError();
        }
        read();

        JsList jsList = new JsList();
        skipWhiteSpaces();
        if (currentByte == ']') {
            read();
            return jsList;
        }

        while (true) {
            skipWhiteSpaces();

            JsValue value = parseValue();
            jsList.apped(value);

            skipWhiteSpaces();
            switch (currentByte) {
                case ',': {
                    read();
                    continue;
                }
                case ']': {
                    read();
                    return jsList;
                }
                default: throw createInvalidJsonError();
            }
        }
    }

    private JsNumber parseNumber() throws IOException, InvalidJsonError {
        skipWhiteSpaces();
        JsNumber jsNumber = new JsNumber();
        ByteArrayOutputStream cache = new ByteArrayOutputStream();

        parseSign(jsNumber);
        parseInt(jsNumber, cache);
        if (currentByte == '.') {
            parseFrac(jsNumber, cache);
        }
        if (currentByte == 'e' || currentByte == 'E') {
            parseExp(jsNumber, cache);
        }

        return jsNumber;
    }

    private void parseSign(JsNumber jsNumber) throws IOException {
        if (currentByte == '+') {
            jsNumber.setPositive(true);
            read();
        } else if (currentByte == '-'){
            jsNumber.setPositive(false);
            read();
        }
    }

    private void parseInt(JsNumber jsNumber, ByteArrayOutputStream cache) throws IOException {
        while(currentByte >= '0' && currentByte <= '9') {
            cache.write(currentByte);
            read();
        }
        jsNumber.setInteger(cache.toByteArray());
        cache.reset();
    }

    private void parseFrac(JsNumber jsNumber, ByteArrayOutputStream cache) throws IOException {
        cache.write(currentByte);
        read();
        while(currentByte > '0' && currentByte < '9') {
            cache.write(currentByte);
            read();
        }
        jsNumber.setFraction(cache.toByteArray());
        cache.reset();
    }

    private void parseExp(JsNumber jsNumber, ByteArrayOutputStream cache) throws IOException, InvalidJsonError {
        cache.write(currentByte);
        read();

        if (currentByte != '+' && currentByte != '-') {
            throw createInvalidJsonError();
        }
        cache.write(currentByte);
        read();

        while(currentByte > '0' && currentByte < '9') {
            cache.write(currentByte);
            read();
        }
        jsNumber.setExp(cache.toByteArray());
        cache.reset();
    }

    private JsNull parseNull() throws InvalidJsonError, IOException {
        if (matchBytes(NULL)) {
            return JsNull.NULL;
        }

        throw createInvalidJsonError();
    }


    private JsBoolean parseTrue() throws InvalidJsonError, IOException {
        if (matchBytes(TRUE)) {
            return JsBoolean.TRUE;
        }

        throw createInvalidJsonError();
    }


    private JsBoolean parseFalse() throws IOException, InvalidJsonError {
        if (matchBytes(FALSE)) {
            return JsBoolean.FALSE;
        }

        throw createInvalidJsonError();
    }

    private JsString parseString() throws InvalidJsonError, IOException {
        skipWhiteSpaces();
        if (currentByte != '"') {
            throw createInvalidJsonError();
        }

        JsString jsString = new JsString();
        boolean needEscape = false;
        while (true) {

            read();
            if (currentByte == -1) {
                throw createInvalidJsonError();
            }

            if (!needEscape && currentByte == '\\') {
                needEscape = true;
                continue;
            } else if (needEscape) {
                jsString.append(currentByte);
                needEscape = false;
                continue;
            }


            if (currentByte == '"') {
                read();
                return jsString;
            }

            if (STRING_UNESCAPE_RANGES.contains(currentByte)) {
                jsString.append(currentByte);
            } else {
                throw createInvalidJsonError();
            }
        }
    }

    private boolean matchBytes(byte[] bytes) throws IOException {
        skipWhiteSpaces();

        for (byte b : bytes) {
            if (currentByte != b) {
                return false;
            }
            read();
        }

        return true;
    }

    private void skipWhiteSpaces() throws IOException {
        while (currentByte == SPACE || currentByte == TAB || currentByte == NEW_LINE || currentByte == RETURN) {
            read();
            if (noMoreBytes()) {
                break;
            }
        }

    }

    private void read() throws IOException {
        currentByte = (byte) in.read();
        if (!noMoreBytes()) {
            readCount += 1;
            cache.write(currentByte);
        }
    }

    private boolean noMoreBytes() {
        return currentByte == -1;
    }

    private InvalidJsonError createInvalidJsonError() {
        return new InvalidJsonError(String.format("JSON invalid at pos %d: \"%s...\"", readCount-1, cache.toString()));
    }
}
