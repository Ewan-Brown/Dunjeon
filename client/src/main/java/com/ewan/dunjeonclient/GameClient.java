package com.ewan.dunjeonclient;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.ewan.meworking.codec.ClientDataEncoder;
import com.ewan.meworking.codec.KryoPreparator;
import com.ewan.meworking.codec.DataFragmentPacketDecoder;
import com.ewan.meworking.data.client.ClientInputData;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class GameClient {

    static Logger logger = LogManager.getLogger();

    public GameClient(ClientChannelHandler clientChannelHandler, String host) {
        int port = 1471;
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
                            new DataFragmentPacketDecoder(kryo),
                            clientChannelHandler);
                }
            });


            logger.info("Client binding to local port ...");
            ChannelFuture f = b.connect(host, port).sync();
            logger.info("Client successfully bound to local port! : " + f.toString());
            f.channel().writeAndFlush(new ClientInputData(List.of()));
            f.channel().closeFuture().sync();
            logger.info("Client disconnected");
            logger.info("f.isCancelled() = " + f.isCancelled());
            logger.info("f.isDone() = " + f.isDone());
        } catch(InterruptedException e) {
            logger.error(e);
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
        }
    }
}