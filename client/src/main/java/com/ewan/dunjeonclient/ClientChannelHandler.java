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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.HashMap;

@Setter
@Getter
public class ClientChannelHandler extends ChannelInboundHandlerAdapter {

    static Logger logger = LogManager.getLogger();

    @Getter
    private BasicMemoryBank clientMemoryBank;
    private double mostRecentTimestampReceived = 0; //TODO Does this really need to be stored if i'm storing mostRecentFrameInfoPacket
    private InetSocketAddress serverAddress;
    private Channel server;

    private HashMap<Integer, GameFrame> gameFrames = new HashMap<>();
    private FrameInfoPacket mostRecentFrameInfoPacket = null;

    public ClientChannelHandler(BasicMemoryBank clientMemoryBank){
        this.clientMemoryBank = clientMemoryBank;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        server = ctx.channel(); //TODO is this necessary every time

        //Check datatype and sort among gameFrames
        int releventTick;
        if(msg instanceof DataPacket data) {
            releventTick = data.getDataWrapper().getTickstamp();
            if(!gameFrames.containsKey(data.getDataWrapper().getTickstamp())){
                gameFrames.put(releventTick, new GameFrame(null));
            }
            if(logger.isDebugEnabled()) {
                int collectedData = gameFrames.get(releventTick).getCollectedData().size();
                String collectString;
                if (gameFrames.get(releventTick).getFramePacket() != null) {
                    collectString = collectedData+"/"+gameFrames.get(releventTick).getFramePacket().expectedDataCount();
                }else{
                    collectString = collectedData+"/?";
                }
                logger.trace("received dataPacket for tick : " + releventTick + " " + collectString);
            }
            gameFrames.get(releventTick).getCollectedData().add(data.getDataWrapper());
        }
        else if(msg instanceof FrameInfoPacket frameInfo) {
            releventTick = frameInfo.worldTimeTicks();
            logger.trace("received frameInfoPacket for tick: " + releventTick);
            if(!gameFrames.containsKey(releventTick)){
                gameFrames.put(releventTick, new GameFrame(frameInfo));
            }else{
                throw new RuntimeException("Duplicate FrameInfoPacket received");
            }
        }else{
            throw new RuntimeException("Unexpected packet type received: " + msg.getClass());
        }

        if (gameFrames.get(releventTick).isComplete()){
            logger.trace("frame for tick: " + releventTick +" is complete");
            mostRecentTimestampReceived = gameFrames.get(releventTick).getFramePacket().worldTimeExact();
            mostRecentFrameInfoPacket = gameFrames.get(releventTick).getFramePacket();
            for (DataWrapper<?,?> collectedDatum : gameFrames.get(releventTick).getCollectedData()) {
                clientMemoryBank.processWrappedData(collectedDatum);
            }
        }
    }

    public void sendMessageToClient(ClientInputData data){
        server.writeAndFlush(data);
    }
}