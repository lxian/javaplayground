package com.playground.hashstore.server.proto.response;

import com.playground.hashstore.server.proto.ByteBufSerializable;
import com.playground.hashstore.server.proto.CommandOPs;
import com.playground.hashstore.server.proto.Constants;
import io.netty.buffer.ByteBuf;

import static com.playground.hashstore.server.proto.Constants.REQID_LEN;

public class GetResponse implements Response, ByteBufSerializable {

    public final byte op = CommandOPs.get;

    public final short id;

    public final boolean success;

    public final byte[] val;

    /**
     * @param id
     * @param success
     * @param val the value or the failure cause
     */
    public GetResponse(short id, boolean success, byte[] val) {
        this.id = id;
        this.success = success;
        this.val = val;
    }

    public String getFailureReason() {
        return new String(val);
    }

    @Override
    public int len() {
        return Constants.OP_LEN + REQID_LEN + Constants.HEAD_LEN + Constants.LEN_INT_LEN + val.length;
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeByte(op);
        byteBuf.writeShort(id);
        byte headByte = (byte)(success ? 0x00 : 0x01);
        byteBuf.writeByte(headByte);
        byteBuf.writeInt(val.length);
        byteBuf.writeBytes(val);
    }

    public static GetResponse read(ByteBuf frame) {
        short id = frame.readShort();
        boolean success = frame.readByte() == 0;
        byte[] val = new byte[frame.readInt()];
        frame.readBytes(val);
        return new GetResponse(id, success, val);
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

