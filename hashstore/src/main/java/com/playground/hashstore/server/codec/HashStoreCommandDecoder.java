package com.playground.hashstore.server.codec;

import com.playground.hashstore.config.ConfigProvider;
import com.playground.hashstore.server.proto.command.GetCommand;
import com.playground.hashstore.server.proto.command.SetCommand;
import com.playground.hashstore.server.proto.CommandOPs;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;


public class HashStoreCommandDecoder extends LengthFieldBasedFrameDecoder {

    public HashStoreCommandDecoder() {
        super(ConfigProvider.config().getMaxFrameSize(), 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        frame.readerIndex(4);
        byte op = frame.readByte();
        switch (op) {
            case CommandOPs.get:
                return GetCommand.read(frame);
            case CommandOPs.set:
                return SetCommand.read(frame);
            default:
                return null;
        }
    }
}
