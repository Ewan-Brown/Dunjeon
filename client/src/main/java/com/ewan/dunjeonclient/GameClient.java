package com.ewan.dunjeonclient;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.ewan.meworking.codec.ClientDataEncoder;
import com.ewan.meworking.codec.KryoPreparator;
import com.ewan.meworking.codec.ServerDataDecoder;
import com.ewan.meworking.data.ClientInputData;
import com.ewan.meworking.data.client.MoveEntity;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.dyn4j.geometry.Vector2;

import java.util.List;

public class GameClient {

    public GameClient(ClientChannelHandler clientChannelHandler, String host) {
        int port = 1469;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Kryo kryo = KryoPreparator.getAKryo();
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioDatagramChannel.class);
            b.handler(new ChannelInitializer<DatagramChannel>() {
                @Override
                public void initChannel(DatagramChannel ch) {
                    ch.pipeline().addLast(
                            new ClientDataEncoder(kryo),
                            new ServerDataDecoder(kryo),
                            clientChannelHandler);
                }
            });

            System.out.println("Client initialization complete");
            ChannelFuture f = b.connect(host, port).sync();
            System.out.println("Sending connection message to server!");
            f.channel().writeAndFlush(new ClientInputData(List.of()));
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