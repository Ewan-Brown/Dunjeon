package com.ewan.meworking.codec;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.ewan.meworking.data.ClientData;
import com.ewan.meworking.data.ServerData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class ClientDataDecoder extends MessageToMessageDecoder<DatagramPacket> {

    private final Kryo kryo;

    public ClientDataDecoder(Kryo kryo) {
        this.kryo = kryo;
    }



    @Override
    protected void decode(ChannelHandlerContext ctx,
                          DatagramPacket msg, List<Object> out){
//        in.content()
//        if (in.readableBytes() < 4)
//            return;
//
//        in.markReaderIndex();
//
//        int completeMessageLength = in.readInt();
//
//        if(in.readableBytes() < completeMessageLength){
//            in.resetReaderIndex();
//        }else{
//            byte[] buf = new byte[completeMessageLength];
//            in.readBytes(buf);
//            out.add(kryo.readObject(new Input(buf), ClientData.class));
//        }
    }
}