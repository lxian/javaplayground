package com.playground.hashstore.server.proto.command;

import com.playground.hashstore.server.proto.ByteBufSerializable;
import com.playground.hashstore.server.proto.CommandOPs;
import io.netty.buffer.ByteBuf;

import static com.playground.hashstore.server.proto.Constants.*;

public class SetCommand implements Command, ByteBufSerializable {
    public final byte op = CommandOPs.set;

    public final short id;

    public final String key;

    public final byte[] val;

    public SetCommand(short id, String key, byte[] val) {
        this.id = id;
        this.key = key;
        this.val = val;
    }

    @Override
    public int len() {
        return OP_LEN + REQID_LEN + LEN_INT_LEN + key.getBytes().length + LEN_INT_LEN + val.length;
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeByte(op);
        byteBuf.writeShort(id);
        byteBuf.writeInt(key.getBytes().length);
        byteBuf.writeBytes(key.getBytes());
        byteBuf.writeInt(val.length);
        byteBuf.writeBytes(val);
    }

    public static SetCommand read(ByteBuf frame) {
        short id = frame.readShort();
        byte[] keyBytes = new byte[frame.readInt()];
        frame.readBytes(keyBytes);
        byte[] valBytes = new byte[frame.readInt()];
        frame.readBytes(valBytes);
        return new SetCommand(id, new String(keyBytes), valBytes);
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
