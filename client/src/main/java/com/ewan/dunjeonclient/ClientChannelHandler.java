package com.ewan.dunjeonclient;

import com.ewan.meworking.codec.ServerDataWrapper;
import com.ewan.meworking.data.ClientInputData;
import com.ewan.meworking.data.ServerData;
import com.ewan.meworking.data.client.UserInput;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.List;

@Setter
@Getter
public class ClientChannelHandler extends ChannelInboundHandlerAdapter {

    private BasicMemoryBank mostRecentBasicMemoryBank = null;
    private double mostRecentWorldTimestamp = 0;
    private InetSocketAddress serverAddress;

    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("ClientChannelHandler.channelRead");
        ServerDataWrapper data = (ServerDataWrapper) msg;
        //Take this data and pass to client's rendering service
    }

    public BasicMemoryBank getMostRecentBasicMemoryBank() {
        return mostRecentBasicMemoryBank;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}