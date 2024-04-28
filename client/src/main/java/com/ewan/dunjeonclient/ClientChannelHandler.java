package com.ewan.dunjeonclient;

import com.ewan.meworking.data.client.ClientInputData;
import com.ewan.meworking.data.server.DataPacket;
import com.ewan.meworking.data.server.EventPacket;
import com.ewan.meworking.data.server.data.DataWrapper;
import com.ewan.meworking.data.server.event.ObservedEvent;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import com.ewan.meworking.data.server.metadata.FrameInfoPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jdk.jfr.Event;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.fraction.FractionConversionException;
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
    private EventManager eventManager;
    private InetSocketAddress serverAddress;
    private Channel server;

    private HashMap<Integer, GameFrame> gameFrames = new HashMap<>();
    private FrameInfoPacket mostRecentFrameInfoPacket = null;
    private boolean isFirstFrame = true;

    public ClientChannelHandler(BasicMemoryBank clientMemoryBank, EventManager manager){
        this.clientMemoryBank = clientMemoryBank;
        this.eventManager = manager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        server = ctx.channel(); //TODO is this necessary every time

        //Check datatype and sort among gameFrames
        int releventTick;
        if(msg instanceof DataPacket data) {
            releventTick = data.getDataWrapper().getTimestamp().serverTick();
            if(!gameFrames.containsKey(releventTick)){
                gameFrames.put(releventTick, new GameFrame(null));
            }
            if(logger.isTraceEnabled()) {
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
            releventTick = frameInfo.timestamp().serverTick();
            if(logger.isTraceEnabled())
                logger.trace("received frameInfoPacket for tick: " + releventTick);
            if(!gameFrames.containsKey(releventTick)){
                gameFrames.put(releventTick, new GameFrame(frameInfo));
            }else{
                throw new RuntimeException("Duplicate FrameInfoPacket received");
            }
        }else if(msg instanceof EventPacket event){
            releventTick = event.getObservedEvent().getTimestamp().serverTick();
            if(!gameFrames.containsKey(releventTick)){
                gameFrames.put(releventTick, new GameFrame(null));
            }
            if(logger.isTraceEnabled()) {
                int collectedEvents = gameFrames.get(releventTick).getCollectedEvents().size();
                String collectString;
                if (gameFrames.get(releventTick).getFramePacket() != null) {
                    collectString = collectedEvents+"/"+gameFrames.get(releventTick).getFramePacket().expectedEventCount();
                }else{
                    collectString = collectedEvents+"/?";
                }
                logger.trace("received eventPacket for tick : " + releventTick + " " + collectString);
            }
            gameFrames.get(releventTick).getCollectedEvents().add(event.getObservedEvent());
        }
        else{
            throw new RuntimeException("Unexpected packet type received: " + msg.getClass());
        }

        if (gameFrames.get(releventTick).isComplete()){
            if(logger.isTraceEnabled())
                logger.trace("frame for tick: " + releventTick +" is complete");
            float updateDelta = 0;
            FrameInfoPacket prevFrameInfoPacket = mostRecentFrameInfoPacket;
            mostRecentFrameInfoPacket = gameFrames.get(releventTick).getFramePacket();
            if(!isFirstFrame){
                updateDelta = mostRecentFrameInfoPacket.timestamp().worldTime() - prevFrameInfoPacket.timestamp().worldTime();
            }
            for (DataWrapper<?,?> collectedDatum : gameFrames.get(releventTick).getCollectedData()) {
                clientMemoryBank.processWrappedData(collectedDatum);
            }
            if(!isFirstFrame) {
                eventManager.updateEvents(updateDelta);
            }
            for (ObservedEvent event : gameFrames.get(releventTick).getCollectedEvents()) {
                eventManager.processEvent(event);
            }
            isFirstFrame = false;
        }
    }

    public void sendMessageToClient(ClientInputData data){
        server.writeAndFlush(data);
    }
}