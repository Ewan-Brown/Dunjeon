package com.ewan.dunjeonclient;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.ewan.meworking.codec.ClientDataEncoder;
import com.ewan.meworking.codec.KryoPreparator;
import com.ewan.meworking.data.ClientData;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.util.List;

public class GameClient {

    public GameClient(ClientChannelHandler clientChannelHandler, String host) {
        int port = 1459;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioDatagramChannel.class);
            b.handler(new ChannelInitializer<DatagramChannel>() {
                @Override
                public void initChannel(DatagramChannel ch) {
                    ch.pipeline().addLast(
                            new ClientDataEncoder());
//                            new ServerDataDecoder(),
//                            clientChannelHandler);
                }
            }).option(ChannelOption.SO_BROADCAST, true);

            System.out.println("Client attempting to connect to server");
            ChannelFuture channelFuture = b.connect("127.0.0.1", 1459).sync();
            channelFuture.channel().writeAndFlush(new ClientData(List.of()));
//            ChannelFuture f = b.connect(host, port).sync();
//            System.out.println("Client Connect complete");
//            f.channel().writeAndFlush(new ClientData(List.of())); //Send a empty client message to initialize client on server-side
//
//            f.channel().closeFuture().sync();
//            System.out.println("f.isCancelled() = " + f.isCancelled());
//            System.out.println("f.isDone() = " + f.isDone());
        } catch(InterruptedException e) {
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
        }
    }
}