package com.ewan.dunjeonclient;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.minlog.Log;
import com.ewan.meworking.codec.ClientDataDecoder;
import com.ewan.meworking.codec.ClientDataEncoder;
import com.ewan.meworking.codec.KryoPreparator;
import com.ewan.meworking.codec.ServerDataDecoder;
import com.ewan.meworking.data.ClientData;
import com.ewan.meworking.data.client.MoveEntity;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.dyn4j.geometry.Vector2;

import java.util.List;

public class GameClient {

    public GameClient(ClientChannelHandler clientChannelHandler, String host) {
        int port = 1459;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Kryo kryo = KryoPreparator.getAKryo();
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                public void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(
                            new ClientDataEncoder(kryo),
                            new ServerDataDecoder(kryo),
                            clientChannelHandler);
                }
            });
            b.option(ChannelOption.SO_KEEPALIVE, true);

            System.out.println("Client attempting to connect to server");
            ChannelFuture f = b.connect(host, port).sync();
            System.out.println("Client Connection to server successful");

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