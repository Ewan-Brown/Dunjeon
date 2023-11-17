package com.ewan.dunjeon.server.networking;

import com.ewan.dunjeon.server.world.Dunjeon;
import com.ewan.dunjeon.server.world.entities.ClientBasedController;
import com.ewan.dunjeon.server.world.entities.creatures.TestSubject;
import com.ewan.meworking.codec.ClientDataDecoder;
import com.ewan.meworking.codec.KryoPreparator;
import com.ewan.meworking.codec.ServerDataEncoder;
import com.esotericsoftware.kryo.kryo5.Kryo;
import com.ewan.meworking.data.ClientData;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.HashMap;

/**
 * Interface between packets and server. Send and receive.
 */
public class ServerManager {

    private static final HashMap<Channel, ClientHandler> clientHandlerHashMap = new HashMap<>();

    public static void runServer(){
//        EventLoopGroup bossGroup = new NioEventLoopGroup();
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
                                    new ServerDataEncoder(kryo),
                                    new ServerManager.ServerInboundChannelHandler());
                        }
                    });

            ChannelFuture f = b.bind(1459).sync();
            f.channel().closeFuture().sync();
        }catch(InterruptedException e){
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
//            bossGroup.shutdownGracefully();
        }
    }

    public static class ServerInboundChannelHandler extends ChannelInboundHandlerAdapter{

        @Override
        @SuppressWarnings("unchecked")
        public void channelActive(final ChannelHandlerContext ctx) {
            System.out.println("Client joined - " + ctx + ", adding to list of client channels");
            ClientBasedController<TestSubject, TestSubject.TestSubjectControls> controller = Dunjeon.getInstance().createClientTestCreatureAndGetController();
            ClientHandler clientHandler = new ClientHandler(controller, ctx.channel());
            clientHandlerHashMap.put(ctx.channel(), clientHandler);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
            System.out.println("Client left - " + ctx + ", removing from list of client channels");
            clientHandlerHashMap.get(ctx.channel()).setConnectionActive(false);
            clientHandlerHashMap.remove(ctx.channel()); //TODO for now...
        }

        @Override
        @SuppressWarnings("unchecked")
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            System.out.println("ServerInboundChannelHandler.channelRead");
            ClientData clientData = (ClientData) msg;
            clientHandlerHashMap.get(ctx.channel()).passInputsToController(clientData.getInputs());
        }
    }

    public static void sendDataToClients(){
        for (ClientHandler handler : clientHandlerHashMap.values()) {
            handler.sendDataToClient();
        }

    }
}
