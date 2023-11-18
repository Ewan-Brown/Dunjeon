package com.ewan.dunjeon.server.networking;

import com.ewan.dunjeon.server.world.Dunjeon;
import com.ewan.dunjeon.server.world.entities.ClientBasedController;
import com.ewan.dunjeon.server.world.entities.creatures.TestSubject;
import com.ewan.meworking.codec.ClientDataDecoder;
import com.ewan.meworking.codec.KryoPreparator;
import com.esotericsoftware.kryo.kryo5.Kryo;
import com.ewan.meworking.data.ClientData;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.util.HashMap;

/**
 * Interface between packets and server. Send and receive.
 */
public class ServerManager {

    private static final HashMap<Channel, ClientHandler> clientHandlerHashMap = new HashMap<>();

    public static void runServer(){
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            Kryo kryo = KryoPreparator.getAKryo();
            b.group(workerGroup)
                    .channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer<DatagramChannel>() {
                        @Override
                        public void initChannel(DatagramChannel ch) {
                            ch.pipeline().addLast(
                                    new ClientDataDecoder(kryo),
                                    new ServerInboundChannelHandler());
                        }
                    }).option(ChannelOption.SO_BROADCAST, true);

            try {
                ChannelFuture channelFuture = b.bind(1459).sync();
                channelFuture.channel();
            } catch (Exception e) {
                throw e;
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static class ServerInboundChannelHandler extends ChannelInboundHandlerAdapter{

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            super.channelRead(ctx, msg);
            System.out.println("ServerInboundChannelHandler.channelRead");
        }
//
//        @Override
//        protected void channelRead0(ChannelHandlerContext ctx, ClientData msg) throws Exception {
//            System.out.println("ServerInboundChannelHandler.channelRead0");
//        }
    }

    public static void sendDataToClients(){
        for (ClientHandler handler : clientHandlerHashMap.values()) {
            handler.sendDataToClient();
        }

    }
}
