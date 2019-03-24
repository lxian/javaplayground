package com.playground.hashstore.server.codec;

import com.playground.hashstore.server.proto.ByteBufSerializable;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class HashStoreByteBufSerializableEncoder extends MessageToByteEncoder<ByteBufSerializable> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBufSerializable msg, ByteBuf out) throws Exception {
        int frameLen = 4 + msg.len();
        out.capacity(frameLen);
        out.writeInt(msg.len());
        msg.write(out);
    }

}
