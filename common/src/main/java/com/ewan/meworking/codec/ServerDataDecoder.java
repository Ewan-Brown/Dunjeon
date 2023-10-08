package com.ewan.meworking.codec;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.ewan.meworking.data.ServerData;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
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
        System.out.println("Attempting to decode!");


        if (in.readableBytes() < 4)
            return;

        in.markReaderIndex();

        int completeMessageLength = in.readInt();
        int currentMessageLength = in.readableBytes();
        System.out.println("completeMessageLength = " + completeMessageLength);
        System.out.println("currentMessageLength = " + currentMessageLength);

        if(in.readableBytes() < completeMessageLength){
            System.out.println("Not long enough. Return!");
            in.resetReaderIndex();
        }else{
            System.out.println("We're good to go!");
            byte[] buf = new byte[completeMessageLength];
            in.readBytes(buf);
            out.add(kryo.readObject(new Input(buf), ServerData.class));
        }

//        int len = in.readUnsignedShort();
//        System.out.println("len: " + len);
//        if (in.readableBytes() < len) {
//            in.resetReaderIndex();
//            return;
//        }
//
//        byte[] buf = new byte[len];
//        in.readBytes(buf);


//        Input input = new Input(buf);
//        Object object = kryo.readObject(input, ServerData.class);
//        out.add(object);
    }
}