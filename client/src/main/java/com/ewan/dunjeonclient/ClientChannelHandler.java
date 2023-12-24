package com.ewan.dunjeonclient;

import com.ewan.meworking.data.ClientInputData;
import com.ewan.meworking.data.ServerData;
import com.ewan.meworking.data.ServerDataWrapper;
import com.ewan.meworking.data.server.data.Data;
import com.ewan.meworking.data.server.data.DataWrapper;
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
    private BasicMemoryBank clientMemoryBank;
    private double mostRecentWorldTimestamp = 0;
    private InetSocketAddress serverAddress;
    private Channel server;

    public ClientChannelHandler(BasicMemoryBank clientMemoryBank){
        this.clientMemoryBank = clientMemoryBank;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        ServerData data = (ServerData) msg;
        server = ctx.channel();
        mostRecentWorldTimestamp = data.getWorldTime();
        System.out.println("# of data wrappers received: " + data.getDataWrappers().size());
        for (DataWrapper<? extends Data,?> dataWrapper : data.getDataWrappers()) {
            clientMemoryBank.processWrappedData(dataWrapper);
        }
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