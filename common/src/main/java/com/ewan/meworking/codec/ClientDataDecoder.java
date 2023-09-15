package com.ewan.meworking.codec;

import com.ewan.meworking.data.ClientData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class ClientDataDecoder extends ReplayingDecoder<ClientData> {

    private final Charset charset = Charset.forName("UTF-8");

    @Override
    protected void decode(ChannelHandlerContext ctx,
                          ByteBuf in, List<Object> out) throws Exception {

        ClientData data = new ClientData();
        data.setIntValue(in.readInt());
        int strLen = in.readInt();
        data.setStringValue(
                in.readCharSequence(strLen, charset).toString());
        out.add(data);
    }
}