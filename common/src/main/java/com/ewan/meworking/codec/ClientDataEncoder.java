package com.ewan.meworking.codec;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.ewan.meworking.data.ClientInputData;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class ClientDataEncoder
        extends MessageToMessageEncoder<ClientInputData> {

    private final Kryo kryo;
    public static final int BUFFER_SIZE = 1500;


    public ClientDataEncoder(Kryo kryo) {
        this.kryo = kryo;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ClientInputData clientInputData, List<Object> list) throws Exception {
        try {
            Output output = new Output(BUFFER_SIZE);
            kryo.writeObject(output, clientInputData);
            list.add(Unpooled.wrappedBuffer(output.getBuffer()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}