package com.ewan.meworking.codec;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.ByteBufferInputStream;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.ewan.meworking.data.server.DataPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataFragmentPacketDecoder extends MessageToMessageDecoder<DatagramPacket> {

    private final Kryo kryo;
    public DataFragmentPacketDecoder(Kryo kryo) {
        this.kryo = kryo;
    }

    Logger logger = LogManager.getLogger();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, DatagramPacket msg, List<Object> out){
        logger.trace("decoding a data fragment");
        Input input = new Input(new ByteBufferInputStream(msg.content().nioBuffer()));
        PacketTypes.PacketType pType = PacketTypes.PacketType.values()[input.readShort()];
        out.add(kryo.readObject(input, pType.relatedClass));
//        if(logger.isTraceEnabled() && pType == PacketTypes.PacketType.DATA_PACKET) {
//            logger.trace("#: "+ input.readInt());
//        }
    }
}