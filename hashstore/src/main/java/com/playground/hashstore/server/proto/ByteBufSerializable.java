package com.playground.hashstore.server.proto;

import io.netty.buffer.ByteBuf;

public interface ByteBufSerializable {

    int len();

    void write(ByteBuf byteBuf);

    // conventional method
    // static write
    // static ? read(ByteBuf byteBuf);
}

