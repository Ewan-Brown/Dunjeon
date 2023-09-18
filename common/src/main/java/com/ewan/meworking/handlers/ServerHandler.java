package com.ewan.meworking.handlers;

import com.ewan.meworking.data.ClientData;
import com.ewan.meworking.data.ServerData;
import io.netty.channel.*;

import java.util.ArrayList;
import java.util.List;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private final List<Channel> clientChannels = new ArrayList<>();
//    private final ChannelGroup clientChannelGroup = new DefaultChannelGroup(); //TODO Should I use this instead of a dumb list of clients

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        System.out.println("Client joined - " + ctx + ", adding to list of client channels");
        clientChannels.add(ctx.channel());
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {

        System.out.println("Server received something from a client, Sending data back to that client just to text things out");

        ClientData clientData = (ClientData) msg;
        ServerData serverData = new ServerData();
        ChannelFuture future = ctx.writeAndFlush(serverData);
    }

    public void writeToClients(ServerData data){
        for (Channel clientChannel : clientChannels) {
            clientChannel.writeAndFlush(data);
        }
    }
}