package com.playground.hashstore.server;

import com.playground.hashstore.HashStore;
import com.playground.hashstore.server.proto.CommandOPs;
import com.playground.hashstore.server.proto.command.Command;
import com.playground.hashstore.server.proto.command.GetCommand;
import com.playground.hashstore.server.proto.command.SetCommand;
import com.playground.hashstore.server.proto.response.GetResponse;
import com.playground.hashstore.server.proto.response.SetResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class HashStoreInboundHandler extends SimpleChannelInboundHandler<Command> {

    private HashStore hashStore;

    public HashStoreInboundHandler(HashStore hashStore) {
        super();
        this.hashStore = hashStore;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command msg) throws Exception {
        switch (msg.getOp()) {
            case CommandOPs.get:
                GetCommand getCommand = (GetCommand)msg;
                byte[] val = hashStore.read(getCommand.key);
                GetResponse getResponse;
                getResponse = new GetResponse(getCommand.id(), true, val == null ? new byte[0]: val);
                ctx.pipeline().writeAndFlush(getResponse);
                break;
            case CommandOPs.set:
                SetCommand setCommand = (SetCommand) msg;
                hashStore.write(setCommand.key, setCommand.val);
                ctx.pipeline().writeAndFlush(new SetResponse(setCommand.id(), true, null));
                break;
            default:
                break;
        }
    }
}
