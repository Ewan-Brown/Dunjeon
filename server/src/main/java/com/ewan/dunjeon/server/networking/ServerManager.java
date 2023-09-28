package com.ewan.dunjeon.server.networking;

import com.esotericsoftware.kryo.kryo5.Serializer;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.ewan.dunjeon.server.world.CellPosition;
import com.ewan.dunjeon.server.world.Dunjeon;
import com.ewan.dunjeon.server.world.entities.ClientBasedController;
import com.ewan.dunjeon.server.world.entities.creatures.TestSubject;
import com.ewan.meworking.codec.ClientDataDecoder;
import com.ewan.meworking.codec.ServerDataEncoder;
import com.esotericsoftware.kryo.kryo5.Kryo;
import com.ewan.meworking.data.ServerData;
import com.ewan.meworking.data.client.ClientAction;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.dyn4j.geometry.Vector2;

import java.util.HashMap;
import java.util.List;

/**
 * Interface between packets and server. Send and receive.
 */
public class ServerManager {

    private static HashMap<Channel, ClientHandler> clientHandlerHashMap = new HashMap<>();

    public static void runServer(){
        System.out.println("ServerManager.runServer");
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new ClientDataDecoder(new Kryo()),
                                    new ServerDataEncoder(new Kryo()),
                                    new ServerManager.ServerInboundChannelHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            System.out.println("ServerManager.runServer 2");
            ChannelFuture f = b.bind(1459).sync();
            f.channel().closeFuture().sync();
        }catch(InterruptedException e){
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
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
        @SuppressWarnings("unchecked")
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            System.out.println("Received some stuff from a client");
            List<ClientAction> clientActions = (List<ClientAction>) msg; // Or so we hope...
            clientHandlerHashMap.get(ctx.channel()).passActionsToController(clientActions);
        }
    }

    public static void sendDataToClients(){
//        System.out.println("ServerManager.sendDataToClients");
        for (ClientHandler handler : clientHandlerHashMap.values()) {
            handler.sendDataToClient();
        }

    }

    //Serializes the player's associated Memory
    public static void serializePlayerData(){
        Kryo kryo = new Kryo();
        Dunjeon dunjeon = Dunjeon.getInstance();
        kryo.setRegistrationRequired(false); //TODO This is easier but performance hit at runtime!
        kryo.setReferences(true);

        kryo.register(Vector2.class, new Serializer<Vector2>() {
            public void write(Kryo kryo, Output output, Vector2 cellPos) {
                output.writeDouble(cellPos.x);
                output.writeDouble(cellPos.y);
            }

            public Vector2 read(Kryo kryo, Input input, Class<? extends Vector2> type) {
                double x = input.readDouble();
                double y = input.readDouble();
                return new Vector2(x, y);
            }

        });
        kryo.register(CellPosition.class, new Serializer<CellPosition>() {
            public void write(Kryo kryo, Output output, CellPosition cellPos) {
                output.writeLong(cellPos.getFloorID());
                kryo.writeClassAndObject(output, cellPos.getPosition());
                output.flush();
            }

            public CellPosition read(Kryo kryo, Input input, Class<? extends CellPosition> type) {
                long floorID = input.readLong();
                Vector2 vector = (Vector2) kryo.readClassAndObject(input);
                return new CellPosition(vector, floorID);
            }

        });

    }
}
