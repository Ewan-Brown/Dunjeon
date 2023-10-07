package com.ewan.meworking.codec;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.ewan.meworking.data.ServerData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ServerDataDecoder
        extends ByteToMessageDecoder{

    private final Kryo kryo;

    public ServerDataDecoder(Kryo kryo) {
        this.kryo = kryo;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx,
                          ByteBuf in, List<Object> out) {
        System.out.println("Decoding a thing!");


        if (in.readableBytes() < 2)
            return;

        in.markReaderIndex();

        int len = in.readUnsignedShort();

        if (in.readableBytes() < len) {
            in.resetReaderIndex();
            return;
        }

        byte[] buf = new byte[len];
        in.readBytes(buf);
        System.out.println("len: " + len);
//        Input input = new Input(buf);
//        Object object = kryo.readObject(input, ServerData.class);
//        out.add(object);
    }
}