package com.ewan.dunjeonclient;

import com.ewan.meworking.data.client.ClientInputData;
import com.ewan.meworking.data.server.DataPacket;
import com.ewan.meworking.data.server.data.Data;
import com.ewan.meworking.data.server.data.DataWrapper;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import com.ewan.meworking.data.server.metadata.FrameInfoPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Setter
@Getter
public class ClientChannelHandler extends ChannelInboundHandlerAdapter {

    @Getter
    private BasicMemoryBank clientMemoryBank;
    private double mostRecentWorldTimestamp = 0;
    private InetSocketAddress serverAddress;
    private Channel server;

//    private HashMap<Long, List<DataWrapper<?,?>>> unprocessedFrames = new HashMap<>();


    private FrameInfoPacket mostRecentFrameInfoPacket;

    public ClientChannelHandler(BasicMemoryBank clientMemoryBank){
        this.clientMemoryBank = clientMemoryBank;
        this.getMostRecentFrameInfoPacket();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        if(msg instanceof DataPacket data) {
            server = ctx.channel();
//            mostRecentWorldTimestamp = data.getWorldTime();
//            clientMemoryBank.processWrappedData(data.getDataWrapper());
        }
        if(msg instanceof FrameInfoPacket frameInfo) {
            server = ctx.channel();

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

    private class FrameBuilder{
        private final int expectedDataWrappers;
        private final double associatedTimestamp;
        private final List<DataWrapper<?,?>> receivedDataWrappers = new ArrayList<>();

        public FrameBuilder(int expectedDataWrappers, double associatedTimestamp) {
            this.expectedDataWrappers = expectedDataWrappers;
            this.associatedTimestamp = associatedTimestamp;
        }
    }
}