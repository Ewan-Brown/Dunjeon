package com.ewan.dunjeonclient;

import com.ewan.meworking.data.ServerData;
import com.ewan.meworking.data.client.ClientAction;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import org.apache.maven.settings.Server;

import java.util.ArrayList;
import java.util.List;
    public class ClientChannelHandler extends ChannelInboundHandlerAdapter {

    private BasicMemoryBank mostRecentBasicMemoryBank = null;
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
        System.out.println("ClientChannelHandler.channelRead");
        System.out.println("msg.getClass() = " + msg.getClass());
        try {
            ServerData data = (ServerData) msg;
            setMostRecentBasicMemoryBank(data.getBasicMemoryBank());
        }catch(Exception e){
            System.out.println("Something bad happened while casting incoming message: " + e.getMessage());
        }
//        setMostRecentBasicMemoryBank(data.getBasicMemoryBank());
    }


    public synchronized BasicMemoryBank getMostRecentBasicMemoryBank() {
        return mostRecentBasicMemoryBank;
    }

    private synchronized void setMostRecentBasicMemoryBank(BasicMemoryBank m){
        if(this.mostRecentBasicMemoryBank == null) {
            this.mostRecentBasicMemoryBank = m;
        }
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