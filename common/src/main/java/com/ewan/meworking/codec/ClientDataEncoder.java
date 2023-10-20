package com.ewan.meworking.codec;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.ewan.meworking.data.ClientData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ClientDataEncoder
        extends MessageToByteEncoder<ClientData> {

    private final Kryo kryo;

    public ClientDataEncoder(Kryo kryo) {
        this.kryo = kryo;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx,
                          ClientData msg, ByteBuf out) {
        System.out.println("Encoding a client message!");

//        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//        Output output = new Output(outStream, 4096);
//
//        kryo.writeObject(output, msg);
//        output.flush();
//
//        byte[] outArray = outStream.toByteArray();
//        out.writeShort(outArray.length);
//        out.writeBytes(outArray);
        out.writeInt(5);
    }
}