package com.ewan.meworking.codec;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.ewan.meworking.data.ServerData;
import com.ewan.meworking.data.ServerDataWrapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ServerDataEncoder
        extends MessageToMessageEncoder<ServerDataWrapper> {

    private final Kryo kryo;

    public ServerDataEncoder(Kryo kryo) {
        this.kryo = kryo;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ServerDataWrapper serverDataWrapper, List<Object> list) throws Exception {
        Output output = new Output(4096);
        kryo.writeObject(output, serverDataWrapper.data());
        list.add(new DatagramPacket(Unpooled.wrappedBuffer(output.getBuffer()), serverDataWrapper.address()));
    }
}