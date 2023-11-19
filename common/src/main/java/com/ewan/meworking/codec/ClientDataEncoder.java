package com.ewan.meworking.codec;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.ewan.meworking.data.ClientData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ClientDataEncoder
        extends MessageToMessageEncoder<ClientData> {

    private final Kryo kryo;

    public ClientDataEncoder(Kryo kryo) {
        this.kryo = kryo;
    }

//    @Override
//    protected void encode(ChannelHandlerContext ctx,
//                          ClientData msg, ByteBuf out) {
//
//        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//        Output output = new Output(outStream, 4096);
//
//        kryo.writeObject(output, msg);
//        output.flush();
//
//        byte[] outArray = outStream.toByteArray();
//        out.writeInt(outArray.length);
//        out.writeBytes(outArray);
//    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ClientData clientData, List<Object> list) throws Exception {
        System.out.println("ClientDataEncoder.encode");
        ByteBuf buffer = Unpooled.buffer();
        Output output = new Output(buffer.maxWritableBytes());
        kryo.writeObject(output, clientData);
        output.flush();
        list.add(buffer);
    }
}