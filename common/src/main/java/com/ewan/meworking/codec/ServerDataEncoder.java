package com.ewan.meworking.codec;

import com.ewan.meworking.data.ServerData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ServerDataEncoder
        extends MessageToByteEncoder<ServerData> {

    @Override
    protected void encode(ChannelHandlerContext ctx,
                          ServerData msg, ByteBuf out) throws Exception {
        out.writeInt(msg.getIntValue());
    }
}