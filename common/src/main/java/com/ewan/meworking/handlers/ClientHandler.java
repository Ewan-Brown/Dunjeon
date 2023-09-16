package com.ewan.meworking.handlers;

import com.ewan.meworking.data.ClientData;
import com.ewan.meworking.data.ServerData;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private Channel serverChannel;

    @Override
    public void channelActive(ChannelHandlerContext ctx)
            throws Exception {
        System.out.println("Client activated, saving channel as server");
        serverChannel = ctx.channel();

        ClientData msg = new ClientData();
        msg.setIntValue(123);
        msg.setStringValue(
                "all work and no play makes jack a dull boy");
        ChannelFuture future = ctx.write(msg);
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        System.out.println("Client received something");
//        System.out.println((ServerData)msg);
//        ctx.close();
    }
}