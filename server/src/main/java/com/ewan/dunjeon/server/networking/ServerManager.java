package com.ewan.dunjeon.server.networking;

import com.ewan.dunjeon.data.Datastreams;
import com.ewan.dunjeon.server.world.Dunjeon;
import com.ewan.dunjeon.server.world.entities.ClientBasedController;
import com.ewan.dunjeon.server.world.entities.creatures.TestSubject;
import com.ewan.meworking.codec.ClientDataDecoder;
import com.ewan.meworking.data.client.ClientInputDataWrapper;
import com.ewan.meworking.codec.KryoPreparator;
import com.ewan.meworking.codec.ServerDataEncoder;
import com.esotericsoftware.kryo.kryo5.Kryo;
import com.ewan.meworking.data.client.DebugInput;
import com.ewan.meworking.data.client.UserInput;
import com.ewan.meworking.data.server.data.Data;
import com.ewan.meworking.data.server.data.Datas;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Interface between packets and server. Send and receive.
 */
public class ServerManager {

    private static final HashMap<InetSocketAddress, ManagedClient> clientHandlerHashMap = new HashMap<>(); //TODO add a thing that removes clients when they disconnect - IF PLAYER CLIENT CRASHES HOW CAN A UDP BASED SERVER KNOW?! MAYBE SEND KEEPALIVE MESSAGES?
    private static Channel channel;
    static Logger logger = LogManager.getLogger();

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

            ChannelFuture f = b.bind(1471).sync();
            f.channel().closeFuture().sync();
        }catch(InterruptedException e){
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
        }
    }

    public static class ServerInboundChannelHandler extends ChannelInboundHandlerAdapter{

        @Override
        @SuppressWarnings("unchecked")
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            super.channelRead(ctx, msg);
            logger.debug("Received a message from client");

            if(channel == null){
                channel = ctx.channel();
            }

            ClientInputDataWrapper dataWrapper = (ClientInputDataWrapper) msg;
            InetSocketAddress address = ((ClientInputDataWrapper) msg).sender();

            for (int i = 0; i < dataWrapper.clientInputData().inputs().size(); i++) {
                UserInput input = dataWrapper.clientInputData().inputs().get(i);
                if(input instanceof DebugInput) {
                    dataWrapper.clientInputData().inputs().remove(input);
                    logger.warn("received debug request!");
                    Datastreams.SightDataStream.do_debug = true;
                }
            }

            if(!clientHandlerHashMap.containsKey(dataWrapper.sender())){

                ClientBasedController<TestSubject, TestSubject.TestSubjectControls> controller = Dunjeon.getInstance().createClientTestCreatureAndGetController();
                clientHandlerHashMap.put(address, new ManagedClient(controller, address));
            }
            //Process input as necessary
            ManagedClient clientController = clientHandlerHashMap.get(address);
            clientController.passInputsToController(dataWrapper.clientInputData().inputs());

        }
    }

    public static void sendDataToClients(){
        for (ManagedClient handler : clientHandlerHashMap.values()) {
            handler.sendDataToClient(channel);
        }

    }
}
