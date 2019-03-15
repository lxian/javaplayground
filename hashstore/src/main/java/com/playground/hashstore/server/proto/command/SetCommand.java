package com.playground.hashstore.server.proto.command;

import com.playground.hashstore.server.proto.ByteBufSerializable;
import com.playground.hashstore.server.proto.CommandOPs;
import io.netty.buffer.ByteBuf;

public class SetCommand implements Command, ByteBufSerializable {
    public final byte op = CommandOPs.set;

    public final String key;

    public final byte[] val;

    public SetCommand(String key, byte[] val) {
        this.key = key;
        this.val = val;
    }

    @Override
    public int len() {
        return 1 + 4 + key.getBytes().length + 4 + val.length;
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeInt(key.getBytes().length);
        byteBuf.writeBytes(key.getBytes());
        byteBuf.writeInt(val.length);
        byteBuf.writeBytes(val);
    }

    public static SetCommand read(ByteBuf frame) {
        byte[] keyBytes = new byte[frame.readInt()];
        frame.readBytes(keyBytes);
        byte[] valBytes = new byte[frame.readInt()];
        frame.readBytes(valBytes);
        return new SetCommand(new String(keyBytes), valBytes);
    }
}
