package com.playground.hashstore.server.proto.command;

import com.playground.hashstore.server.proto.ByteBufSerializable;
import com.playground.hashstore.server.proto.CommandOPs;
import io.netty.buffer.ByteBuf;

public class GetCommand implements Command, ByteBufSerializable {
    public final byte op = CommandOPs.get;

    public final String key;

    public GetCommand(String key) {
        this.key = key;
    }

    @Override
    public int len() {
        return 1 + 4 + key.getBytes().length;
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeByte(op);
        byteBuf.writeInt(key.getBytes().length);
        byteBuf.writeBytes(key.getBytes());
    }

    public static GetCommand read(ByteBuf frame) {
        byte[] bytes = new byte[frame.readInt()];
        frame.readBytes(bytes);
        return new GetCommand(new String(bytes));
    }
}
