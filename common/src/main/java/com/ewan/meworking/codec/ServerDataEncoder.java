package com.ewan.meworking.codec;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.ewan.meworking.data.ServerData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ServerDataEncoder
        extends MessageToMessageEncoder<ServerData> {

    private final Kryo kryo;

    public ServerDataEncoder(Kryo kryo) {
        this.kryo = kryo;
    }

//    @Override
//    protected void encode(ChannelHandlerContext ctx,
//                          ServerData msg, ByteBuf out) throws Exception{
//
//        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//        Output output = new Output(outStream, 4096);
//
//        kryo.writeObject(output, msg);
//        output.flush();
//        byte[] outArray = outStream.toByteArray();
//        out.writeInt(outArray.length);
//        out.writeBytes(outArray);
//    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ServerData serverData, List<Object> list) throws Exception {
        System.out.println("ServerDataEncoder.encode");
    }
}