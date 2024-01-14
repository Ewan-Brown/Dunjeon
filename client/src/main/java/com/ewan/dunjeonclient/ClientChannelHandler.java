package com.ewan.dunjeonclient;

import com.ewan.meworking.data.client.ClientInputData;
import com.ewan.meworking.data.server.DataPacket;
import com.ewan.meworking.data.server.data.DataWrapper;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import com.ewan.meworking.data.server.metadata.FrameInfoPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.HashMap;

@Setter
@Getter
public class ClientChannelHandler extends ChannelInboundHandlerAdapter {

    @Getter
    private BasicMemoryBank clientMemoryBank;
    private double mostRecentTimestampReceived = 0;
    private InetSocketAddress serverAddress;
    private Channel server;

    private HashMap<Integer, GameFrame> gameFrames = new HashMap<>();

    private FrameInfoPacket mostRecentFrameInfoPacket;

    public ClientChannelHandler(BasicMemoryBank clientMemoryBank){
        this.clientMemoryBank = clientMemoryBank;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        server = ctx.channel(); //TODO is this necessary

        //Check datatype and sort among gameFrames
        System.out.println("ClientChannelHandler.channelRead");
        int releventTick;
        if(msg instanceof DataPacket data) {
            System.out.println("DataPacket received = " + data);
            releventTick = data.getDataWrapper().getTickstamp();
            if(!gameFrames.containsKey(data.getDataWrapper().getTickstamp())){
                gameFrames.put(releventTick, new GameFrame(null));
            }
            gameFrames.get(releventTick).getCollectedData().add(data.getDataWrapper());

        }
        else if(msg instanceof FrameInfoPacket frameInfo) {
            System.out.println("FrameInfoPacket received = " + frameInfo);
            releventTick = frameInfo.worldTimeTicks();
            if(!gameFrames.containsKey(releventTick)){
                gameFrames.put(releventTick, new GameFrame(frameInfo));
            }else{
                throw new RuntimeException("Duplicate FrameInfoPacket received");
            }
        }else{
            throw new RuntimeException("Unexpected packet type received: " + msg.getClass());
        }

        if (gameFrames.get(releventTick).isComplete()){
            mostRecentTimestampReceived = gameFrames.get(releventTick).getFramePacket().worldTimeExact();
            System.out.println("Game frame complete for tick " + releventTick);
            for (DataWrapper<?,?> collectedDatum : gameFrames.get(releventTick).getCollectedData()) {
                clientMemoryBank.processWrappedData(collectedDatum);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public void sendMessageToClient(ClientInputData data){
        server.writeAndFlush(data);
    }
}