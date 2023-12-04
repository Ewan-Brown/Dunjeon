package com.ewan.dunjeonclient;

import com.ewan.meworking.data.ClientInputData;
import com.ewan.meworking.data.ServerData;
import com.ewan.meworking.data.ServerDataWrapper;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;

@Setter
@Getter
public class ClientChannelHandler extends ChannelInboundHandlerAdapter {

    @Getter
    private BasicMemoryBank mostRecentBasicMemoryBank = null;
    private double mostRecentWorldTimestamp = 0;
    private InetSocketAddress serverAddress;
    private Channel server;

    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ServerData data = (ServerData) msg;
        server = ctx.channel();
        mostRecentBasicMemoryBank = data.getBasicMemoryBank();
        mostRecentWorldTimestamp = data.getWorldTime();
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