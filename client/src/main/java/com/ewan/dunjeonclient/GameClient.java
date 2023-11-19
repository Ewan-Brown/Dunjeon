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
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
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
//                            new ClientDataEncoder(kryo),
//                            new ServerDataDecoder(kryo),
                            clientChannelHandler);
                }
            });

            System.out.println("Client initialization complete");
            ChannelFuture f = b.connect(host, port).sync();
            System.out.println("Sending test data to server, just some bytes :)");
            f.channel().writeAndFlush(Unpooled.wrappedBuffer("Hello".getBytes())).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    System.out.println(future.isSuccess()? "Message sent to server : Hello" : "Message sending failed");
                }
            });
//            f.channel().writeAndFlush(new ClientData(List.of(new MoveEntity(new Vector2()))));
            System.out.println("Client connect call complete, retrieving future sync");
//            clientChannelHandler.setServerChannel(f.channel());
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