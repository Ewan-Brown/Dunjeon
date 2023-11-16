package com.ewan.meworking.codec;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.ewan.meworking.data.ServerData;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ServerDataDecoder extends ByteToMessageDecoder{

    private final Kryo kryo;

    public ServerDataDecoder(Kryo kryo) {
        this.kryo = kryo;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx,
                          ByteBuf in, List<Object> out) throws Exception{

        System.out.println("ServerDataDecoder.decode");
        if (in.readableBytes() < 4)
            return;

        in.markReaderIndex();

        int completeMessageLength = in.readInt();

        if(in.readableBytes() < completeMessageLength){
            in.resetReaderIndex();
        }else{
            byte[] buf = new byte[completeMessageLength];
            in.readBytes(buf);
            out.add(kryo.readObject(new Input(buf), ServerData.class));
        }
    }
}