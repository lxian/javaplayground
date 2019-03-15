package com.playground.hashstore.server.client;

import com.playground.hashstore.server.codec.HashStoreClientCodec;
import com.playground.hashstore.server.proto.command.GetCommand;
import com.playground.hashstore.server.proto.command.SetCommand;
import com.playground.hashstore.server.proto.response.GetResponse;
import com.playground.hashstore.server.proto.response.Response;
import com.playground.hashstore.server.proto.response.SetResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class HashStoreClient {

    private Logger logger = LoggerFactory.getLogger(HashStoreClient.class);

    private String host;
    private int port;
    private Channel channel;

    public HashStoreClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup clientGroup = new NioEventLoopGroup();
        bootstrap
                .group(clientGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new HashStoreClientCodec())
                                .addLast(new ClientIncomingResponseHandler());
                    }
                });

        try {
            ChannelFuture f = bootstrap.connect(host, port).sync();
            channel = f.channel();
            f.channel().closeFuture().addListener(future -> clientGroup.shutdownGracefully());
        } catch (InterruptedException e) {
            clientGroup.shutdownGracefully();
        }
    }

    public void close() {
        channel.close();
    }

    private final Map<Short, ResultHolder> waitingRequests = new ConcurrentHashMap<>();

    private AtomicInteger reqId = new AtomicInteger(1);
    private short nextReqId() {
        return (short)reqId.getAndIncrement();
    }


    public byte[] get(String key) throws HashStoreError {
        final short reqId = nextReqId();
        channel.writeAndFlush(new GetCommand(reqId, key));
        final ResultHolder holder = new ResultHolder();
        waitingRequests.put(reqId, holder);
        waitForResult(holder);

        waitingRequests.remove(reqId);
        GetResponse getResponse = ((GetResponse) holder.getResponse());
        if (getResponse.success) {
            return getResponse.val;
        } else {
            throw new HashStoreError(getResponse.getFailureReason());
        }
    }

    public void set(String key, byte[] value) throws HashStoreError {
        final short reqId = nextReqId();
        channel.writeAndFlush(new SetCommand(reqId, key, value));
        final ResultHolder holder = new ResultHolder();
        waitingRequests.put(reqId, holder);
        waitForResult(holder);

        waitingRequests.remove(reqId);
        SetResponse setResponse = ((SetResponse) holder.getResponse());
        if (!setResponse.success) {
            throw new HashStoreError(setResponse.reason);
        }
    }

    private void waitForResult(ResultHolder holder) {
        synchronized (waitingRequests) {
            while (holder.getResponse() == null) {
                try {
                    waitingRequests.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ClientIncomingResponseHandler extends SimpleChannelInboundHandler<Response> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Response msg) throws Exception {
            waitingRequests.get(msg.id()).setResponse(msg);
            synchronized (waitingRequests) {
                waitingRequests.notifyAll();
            }
        }
    }

}
