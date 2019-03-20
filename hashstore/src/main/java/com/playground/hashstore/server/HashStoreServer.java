package com.playground.hashstore.server;


import com.playground.hashstore.HashStore;
import com.playground.hashstore.config.ConfigProvider;
import com.playground.hashstore.server.codec.HashStoreCommandDecoder;
import com.playground.hashstore.server.codec.HashStoreServerCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class HashStoreServer {

    private HashStore hashStore;

    private Channel channel;

    public HashStoreServer(HashStore hashStore) {
        this.hashStore = hashStore;
    }

    private Thread thread;

    public void startInBackground() {
        thread = new Thread(this::start);
        thread.setDaemon(true);
        thread.setName("hashstore-server-main");
        thread.start();
    }

    public void start() {
        hashStore.start();

        InetSocketAddress inetSocketAddress = new InetSocketAddress(ConfigProvider.config().getPort());
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group).channel(NioServerSocketChannel.class)
                    .localAddress(inetSocketAddress)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new HashStoreServerCodec())
                                    .addLast(new HashStoreInboundHandler(hashStore));
                        }
                    });

            ChannelFuture channelFuture = bootstrap.bind().sync();
            channel = channelFuture.channel();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            hashStore.close();
        }

    }

    public void close() {
        hashStore.close();
        channel.close();
    }
}
