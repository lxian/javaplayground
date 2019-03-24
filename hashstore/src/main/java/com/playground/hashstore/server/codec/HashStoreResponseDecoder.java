package com.playground.hashstore.server.codec;

import com.playground.hashstore.config.ConfigProvider;
import com.playground.hashstore.server.proto.CommandOPs;
import com.playground.hashstore.server.proto.response.GetResponse;
import com.playground.hashstore.server.proto.response.SetResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;


public class HashStoreResponseDecoder extends LengthFieldBasedFrameDecoder {

    public HashStoreResponseDecoder() {
        super(ConfigProvider.config().getMaxFrameSize(), 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        frame.readerIndex(4);
        byte op = frame.readByte();
        switch (op) {
            case CommandOPs.get:
                return GetResponse.read(frame);
            case CommandOPs.set:
                return SetResponse.read(frame);
            default:
                return null;
        }
    }
}
