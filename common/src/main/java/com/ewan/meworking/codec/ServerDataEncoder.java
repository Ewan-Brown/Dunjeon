package com.ewan.meworking.codec;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.ewan.meworking.data.ServerData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;

public class ServerDataEncoder
        extends MessageToByteEncoder<ServerData> {

    private final Kryo kryo;

    public ServerDataEncoder(Kryo kryo) {
        this.kryo = kryo;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx,
                          ServerData msg, ByteBuf out) throws Exception{

        System.out.println("Encoding a thing!");
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        Output output = new Output(outStream, 4096);

        kryo.writeObject(output, msg);
        output.flush();
        byte[] outArray = outStream.toByteArray();
        System.out.println("Encoded array len: " + outArray.length);
        out.writeInt(outArray.length);
        out.writeBytes(outArray);
    }
}