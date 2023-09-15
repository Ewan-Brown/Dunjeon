package com.ewan.meworking.codec;

import com.ewan.meworking.data.ServerData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class ServerDataDecoder
        extends ReplayingDecoder<ServerData> {

    @Override
    protected void decode(ChannelHandlerContext ctx,
                          ByteBuf in, List<Object> out) {

        ServerData data = new ServerData();
        data.setIntValue(in.readInt());
        out.add(data);
    }
}