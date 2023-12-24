package com.ewan.meworking.codec;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.ByteBufferInputStream;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.ewan.meworking.data.ClientInputData;
import com.ewan.meworking.data.ServerData;
import com.ewan.meworking.data.ServerDataWrapper;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DatagramPacketDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class ServerDataDecoder extends MessageToMessageDecoder<DatagramPacket> {

    private final Kryo kryo;

    public ServerDataDecoder(Kryo kryo) {
        this.kryo = kryo;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, DatagramPacket msg, List<Object> out){
        ServerData data = kryo.readObject(new Input(new ByteBufferInputStream(msg.content().nioBuffer())), ServerData.class);
        out.add(data);
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