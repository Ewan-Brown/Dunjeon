package com.ewan.meworking.codec;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.ewan.meworking.data.server.ServerPacketWrapper;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static com.ewan.meworking.codec.ClientDataEncoder.BUFFER_SIZE;

public class ServerDataEncoder
        extends MessageToMessageEncoder<ServerPacketWrapper> {

    private final Kryo kryo;
    static Logger logger = LogManager.getLogger();

    public ServerDataEncoder(Kryo kryo) {
        this.kryo = kryo;
    }

//    int i = 0;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ServerPacketWrapper serverPacketWrapper, List<Object> list) throws Exception {

        try {
            Output output = new Output(BUFFER_SIZE);
            output.writeShort(serverPacketWrapper.pType().ordinal()); //TODO This is not ideal, absolutely feeble in fact. Atleast use a char or something more debuggable.
            kryo.writeObject(output, serverPacketWrapper.data());
//            if(serverPacketWrapper.pType() == PacketTypes.PacketType.DATA_PACKET) {
//                output.writeInt(i);//TODO REMOVE, USED FOR DEBUGGING!
//                i++;
//            }
            list.add(new DatagramPacket(Unpooled.wrappedBuffer(output.getBuffer()), serverPacketWrapper.address()));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

}