package com.ewan.meworking.codec;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.ByteBufferInputStream;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.ewan.meworking.data.ClientInputData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class ClientDataDecoder extends MessageToMessageDecoder<DatagramPacket> {

    private final Kryo kryo;

    public ClientDataDecoder(Kryo kryo) {
        this.kryo = kryo;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        System.out.println("ClientDataDecoder.decode");
        ClientInputData data = kryo.readObject(new Input(new ByteBufferInputStream(msg.content().nioBuffer())), ClientInputData.class);
        ClientInputDataWrapper wrapper = new ClientInputDataWrapper(data, msg.sender());
        out.add(wrapper);
    }

}