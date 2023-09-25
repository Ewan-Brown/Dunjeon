package com.ewan.meworking.handlers;

import com.ewan.meworking.data.client.ClientAction;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ClientChannelHandler extends ChannelInboundHandlerAdapter {

    private Channel serverChannel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Client activated, saving channel as server");
        serverChannel = ctx.channel();
        ctx.flush();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("Client received something");

    }
}