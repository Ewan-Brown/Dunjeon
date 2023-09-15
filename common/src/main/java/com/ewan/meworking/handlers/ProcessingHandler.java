package com.ewan.meworking.handlers;

import com.ewan.meworking.data.ClientData;
import com.ewan.meworking.data.ServerData;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ProcessingHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {

        ClientData clientData = (ClientData) msg;
        ServerData serverData = new ServerData();
        serverData.setIntValue(clientData.getIntValue() * 2);
        ChannelFuture future = ctx.writeAndFlush(serverData);
        future.addListener(ChannelFutureListener.CLOSE);
        System.out.println(clientData);
    }
}