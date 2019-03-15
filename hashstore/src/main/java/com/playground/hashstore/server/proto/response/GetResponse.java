package com.playground.hashstore.server.proto.response;

import com.playground.hashstore.server.proto.ByteBufSerializable;
import com.playground.hashstore.server.proto.CommandOPs;
import io.netty.buffer.ByteBuf;

public class GetResponse implements Response, ByteBufSerializable {

    public final byte op = CommandOPs.get;

    public final boolean success;

    public final byte[] val;

    public GetResponse(boolean success, byte[] val) {
        this.success = success;
        this.val = val;
    }

    @Override
    public int len() {
        return 4 + 4 + val.length;
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byte headByte = (byte)(success ? 0x00 : 0x01);
        byteBuf.writeByte(headByte);
        byteBuf.writeInt(val.length)
        byteBuf.writeBytes(val);
    }
}

