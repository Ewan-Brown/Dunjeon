package com.ewan.dunjeonclient;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.minlog.Log;
import com.ewan.meworking.codec.ClientDataEncoder;
import com.ewan.meworking.codec.ServerDataDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class GameClient {

    public GameClient(ClientChannelHandler clientChannelHandler) {
        String host = "localhost";
        int port = 1459;
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Log.TRACE();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                public void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new ClientDataEncoder(new Kryo()),
                            new ServerDataDecoder(new Kryo()), clientChannelHandler);
                }
            });
            b.option(ChannelOption.SO_KEEPALIVE, true);

            System.out.println("Attempting to connect");
            ChannelFuture f = b.connect(host, port).sync();
            System.out.println("Connection attempt completed");
            f.channel().closeFuture().sync();
            System.out.println("f.isCancelled() = " + f.isCancelled());
            System.out.println("f.isDone() = " + f.isDone());
        } catch(InterruptedException e) {
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
        }
    }
}