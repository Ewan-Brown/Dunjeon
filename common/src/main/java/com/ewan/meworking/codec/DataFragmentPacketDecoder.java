package com.ewan.meworking.codec;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.ByteBufferInputStream;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.ewan.meworking.data.server.DataPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class DataFragmentPacketDecoder extends MessageToMessageDecoder<DatagramPacket> {

    private final Kryo kryo;
    public DataFragmentPacketDecoder(Kryo kryo) {
        this.kryo = kryo;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, DatagramPacket msg, List<Object> out){
        Input input = new Input(new ByteBufferInputStream(msg.content().nioBuffer()));
        PacketTypes.PacketType pType = PacketTypes.PacketType.values()[input.readShort()];
        out.add(kryo.readObject(input, pType.relatedClass));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        try {
            super.exceptionCaught(ctx, cause);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}