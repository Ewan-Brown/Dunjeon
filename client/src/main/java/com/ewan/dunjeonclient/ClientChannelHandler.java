package com.ewan.dunjeonclient;

import com.ewan.meworking.data.ClientData;
import com.ewan.meworking.data.ServerData;
import com.ewan.meworking.data.client.UserInput;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ClientChannelHandler extends ChannelInboundHandlerAdapter {

    private BasicMemoryBank mostRecentBasicMemoryBank = null;
    private double mostRecentWorldTimestamp = 0;
    private Channel serverChannel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("ClientChannelHandler.channelActive");
//        serverChannel = ctx.channel();
//        ctx.flush();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("ClientChannelHandler.channelRead");
//        ServerData data = (ServerData) msg;
//        setMostRecentBasicMemoryBank(data.getBasicMemoryBank());
//        setMostRecentWorldTimestamp(data.getWorldTime());
    }

    public void sendSingleInputToServer(UserInput input){
        ClientData cData = new ClientData(List.of(input));
        serverChannel.writeAndFlush(cData);
    }

    public BasicMemoryBank getMostRecentBasicMemoryBank() {
        return mostRecentBasicMemoryBank;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        System.out.println("ClientChannelHandler.channelReadComplete");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}