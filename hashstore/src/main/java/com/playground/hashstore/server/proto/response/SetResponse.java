package com.playground.hashstore.server.proto.response;

import com.playground.hashstore.server.proto.ByteBufSerializable;
import com.playground.hashstore.server.proto.CommandOPs;
import io.netty.buffer.ByteBuf;

import static com.playground.hashstore.server.proto.Constants.*;

public class SetResponse implements Response, ByteBufSerializable {

    public final byte op = CommandOPs.set;

    public final short id;

    public final boolean success;

    public final String reason;

    /**
     * @param id
     * @param success
     * @param reason
     */
    public SetResponse(short id, boolean success, String reason) {
        this.id = id;
        this.success = success;
        this.reason = reason;
    }

    @Override
    public int len() {
        return OP_LEN + REQID_LEN + HEAD_LEN + ((reason == null || reason.isEmpty()) ? 0 : LEN_INT_LEN + reason.getBytes().length);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeByte(op);
        byteBuf.writeShort(id);
        byte headByte = (byte)(success ? 0x00 : 0x01);
        byteBuf.writeByte(headByte);
        if (reason != null && !reason.isEmpty()) {
            byteBuf.writeInt(reason.getBytes().length);
            byteBuf.writeBytes(reason.getBytes());
        }
    }

    public static SetResponse read(ByteBuf frame) {
        short id = frame.readShort();
        boolean success = frame.readByte() == 0;
        String reason = null;
        if (!success) {
            byte[] bytes = new byte[frame.readInt()];
            frame.readBytes(bytes);
            reason = new String(bytes);
        }
        return new SetResponse(id, success, reason);
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
