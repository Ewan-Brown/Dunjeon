package com.ewan.dunjeonclient;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.ewan.meworking.codec.ClientDataEncoder;
import com.ewan.meworking.codec.ServerDataDecoder;
import com.ewan.meworking.handlers.ClientChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class GameClient {
    public static void main(String[] args) throws Exception {

        String host = "localhost";
        int port = 1459;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                public void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new ClientDataEncoder(new Kryo()),
                            new ServerDataDecoder(new Kryo()), new ClientChannelHandler());
                }
            });

            ChannelFuture f = b.connect(host, port).sync();

            f.channel().closeFuture().sync();
            System.out.println("f.isCancelled() = " + f.isCancelled());
            System.out.println("f.isDone() = " + f.isDone());
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}