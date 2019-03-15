package com.playground.hashstore.server.proto.command;

import com.playground.hashstore.server.proto.ByteBufSerializable;
import com.playground.hashstore.server.proto.CommandOPs;
import io.netty.buffer.ByteBuf;

import static com.playground.hashstore.server.proto.Constants.*;

public class GetCommand implements Command, ByteBufSerializable {
    public final byte op = CommandOPs.get;

    public final short id;

    public final String key;

    public GetCommand(short id, String key) {
        this.id = id;
        this.key = key;
    }

    @Override
    public int len() {
        return OP_LEN + REQID_LEN + LEN_INT_LEN + key.getBytes().length;
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeByte(op);
        byteBuf.writeShort(id);
        byteBuf.writeInt(key.getBytes().length);
        byteBuf.writeBytes(key.getBytes());
    }

    public static GetCommand read(ByteBuf frame) {
        short id = frame.readShort();
        byte[] bytes = new byte[frame.readInt()];
        frame.readBytes(bytes);
        return new GetCommand(id, new String(bytes));
    }

    @Override
    public byte getOp() {
        return op;
    }

    @Override
    public short id() {
        return id;
    }
}
