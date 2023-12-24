package com.ewan.dunjeon.server.networking;

import com.ewan.dunjeon.server.world.Dunjeon;
import com.ewan.dunjeon.server.world.entities.ClientBasedController;
import com.ewan.dunjeon.server.world.entities.creatures.TestSubject;
import com.ewan.meworking.codec.ClientDataDecoder;
import com.ewan.meworking.codec.ClientInputDataWrapper;
import com.ewan.meworking.codec.KryoPreparator;
import com.ewan.meworking.codec.ServerDataEncoder;
import com.esotericsoftware.kryo.kryo5.Kryo;
import com.ewan.meworking.data.ServerData;
import com.ewan.meworking.data.ServerDataWrapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Interface between packets and server. Send and receive.
 */
public class ServerManager {

    private static final HashMap<InetSocketAddress, ManagedClient> clientHandlerHashMap = new HashMap<>();
    private static Channel channel;

    public static void runServer(){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            Kryo kryo = KryoPreparator.getAKryo();
            b.group(bossGroup)
                    .channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer<DatagramChannel>() {
                        public void initChannel(DatagramChannel ch) {
                            ch.pipeline().addLast(
                                    new ClientDataDecoder(kryo),
                                    new ServerDataEncoder(kryo),
                                    new ServerInboundChannelHandler());
                        }
                    }).option(ChannelOption.AUTO_CLOSE, true)
                    .option(ChannelOption.SO_BROADCAST, true);

            ChannelFuture f = b.bind(1469).sync();
            f.channel().closeFuture().sync();
        }catch(InterruptedException e){
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
        }
    }

    public static class ServerInboundChannelHandler extends ChannelInboundHandlerAdapter{

        @Override
        @SuppressWarnings("unchecked")
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            super.channelRead(ctx, msg);

            if(channel == null){
                channel = ctx.channel();
            }

            ClientInputDataWrapper dataWrapper = (ClientInputDataWrapper) msg;
            InetSocketAddress address = ((ClientInputDataWrapper) msg).sender();
            if(!clientHandlerHashMap.containsKey(dataWrapper.sender())){
                System.out.println("Unknown client has messaged server, sending ba ck a simple acknowledge");

                //Sending a test message just for fun
                ctx.channel().writeAndFlush(new ServerDataWrapper(new ServerData(new ArrayList<>(), 0), address));
                ClientBasedController<TestSubject, TestSubject.TestSubjectControls> controller = Dunjeon.getInstance().createClientTestCreatureAndGetController();
                clientHandlerHashMap.put(address, new ManagedClient(controller, address));
            }
            {
                //Process input as necessary
                ManagedClient clientController = clientHandlerHashMap.get(address);
                clientController.passInputsToController(dataWrapper.clientInputData().inputs());

            }

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }

    public static void sendDataToClients(){
        System.out.println("ServerManager.sendDataToClients");
        for (ManagedClient handler : clientHandlerHashMap.values()) {
            handler.sendDataToClient(channel);
        }

    }
}
