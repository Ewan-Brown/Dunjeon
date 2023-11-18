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
    private ChannelHandlerContext serverChannelContext;

    int successCount = 0;
    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        successCount++;
        System.out.println("ClientChannelHandler.channelRead - Succesfully received a message from server, " + successCount +" messages decoded so far");
        ServerData data = (ServerData) msg;
        if(data.getBasicMemoryBank() != null) {
            setMostRecentBasicMemoryBank(data.getBasicMemoryBank());
        }else{
            System.out.println("memory null, worldtime=" + data.getWorldTime());
        }
        setMostRecentWorldTimestamp(data.getWorldTime());
    }

    public void sendSingleInputToServer(UserInput input){
        System.out.println("sending data to server with ctx:" + serverChannelContext.toString());
        ClientData cData = new ClientData(List.of(input));
        serverChannelContext.writeAndFlush(cData);
    }

    public BasicMemoryBank getMostRecentBasicMemoryBank() {
        return mostRecentBasicMemoryBank;
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