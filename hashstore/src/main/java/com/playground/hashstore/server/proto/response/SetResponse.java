package com.playground.hashstore.server.proto.response;

import com.playground.hashstore.server.proto.ByteBufSerializable;
import com.playground.hashstore.server.proto.CommandOPs;
import io.netty.buffer.ByteBuf;

public class SetResponse implements Response, ByteBufSerializable {

    public final byte op = CommandOPs.set;

    public final boolean success;

    public final String reason;

    public SetResponse(boolean success, String reason) {
        this.success = success;
        this.reason = reason;
    }

    @Override
    public int len() {
        return 0;
    }

    @Override
    public void write(ByteBuf byteBuf) {

    }
}
