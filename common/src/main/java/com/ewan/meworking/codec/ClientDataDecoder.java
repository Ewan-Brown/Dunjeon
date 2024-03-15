package com.ewan.meworking.codec;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.ByteBufferInputStream;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.ewan.meworking.data.client.ClientInputData;
import com.ewan.meworking.data.client.ClientInputDataWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ClientDataDecoder extends MessageToMessageDecoder<DatagramPacket> {

    private final Kryo kryo;
    static Logger logger = LogManager.getLogger();

    public ClientDataDecoder(Kryo kryo) {
        this.kryo = kryo;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) {
        logger.trace("decoding incoming message");
        try{
            ClientInputData data = kryo.readObject(new Input(new ByteBufferInputStream(msg.content().nioBuffer())), ClientInputData.class);
            ClientInputDataWrapper wrapper = new ClientInputDataWrapper(data, msg.sender());
            out.add(wrapper);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

}