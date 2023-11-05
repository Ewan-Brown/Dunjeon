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

public class ClientChannelHandler extends ChannelInboundHandlerAdapter {

    private BasicMemoryBank mostRecentBasicMemoryBank = null;
    @Setter
    @Getter
    private Channel serverChannel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Client activated, saving channel as server");
        serverChannel = ctx.channel();
        ctx.flush();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ServerData data = (ServerData) msg;
        setMostRecentBasicMemoryBank(data.getBasicMemoryBank());
    }

    public void sendSingleInputToServer(UserInput input){
        ClientData cData = new ClientData(List.of(input));
        serverChannel.writeAndFlush(cData);
    }

    public BasicMemoryBank getMostRecentBasicMemoryBank() {
        return mostRecentBasicMemoryBank;
    }

    private void setMostRecentBasicMemoryBank(BasicMemoryBank m){
        this.mostRecentBasicMemoryBank = m;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}