package com.ewan.meworking.codec;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.ewan.meworking.data.ClientData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class ClientDataDecoder extends ReplayingDecoder<ClientData> {

    private final Kryo kryo;

    public ClientDataDecoder(Kryo kryo) {
        this.kryo = kryo;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx,
                          ByteBuf in, List<Object> out) throws Exception {

//        if (in.readableBytes() < 2)
//            return;
//
//        in.markReaderIndex();
//
//        int len = in.readUnsignedShort();
//
//        if (in.readableBytes() < len) {
//            in.resetReaderIndex();
//            return;
//        }
//
//        byte[] buf = new byte[len];
//        in.readBytes(buf);
//        Input input = new Input(buf);
//        Object object = kryo.readObject(input, ClientData.class);
//        out.add(object);
    }
}