package com.playground.hashstore.server;

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
        byte op = frame.readByte();
        switch (op) {
            case CommandOPs.get:
                byte[] bytes = new byte[frame.readInt()];
                frame.readBytes(bytes);
                return new GetCommand(new String(bytes));
            case CommandOPs.set:
                byte[] keyBytes = new byte[frame.readInt()];
                frame.readBytes(keyBytes);
                byte[] valBytes = new byte[frame.readInt()];
                frame.readBytes(valBytes);
                return new SetCommand(new String(keyBytes), valBytes);
            default:
                return null;
        }
    }
}
