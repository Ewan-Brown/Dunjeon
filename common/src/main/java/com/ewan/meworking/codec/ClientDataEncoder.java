package com.ewan.meworking.codec;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.ewan.meworking.data.ClientData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.util.List;

public class ClientDataEncoder extends MessageToMessageEncoder<ClientData> {

    Kryo kryo = KryoPreparator.getAKryo();

    @Override
    protected void encode(ChannelHandlerContext ctx, ClientData msg, List<Object> out) throws Exception {
        System.out.println("ClientDataEncoder.encode");
//        ByteBuf buffer = Unpooled.buffer();
//        Output output = new Output((int)buffer.maxWritableBytes());
//        kryo.writeObject(output, msg);
//        output.flush();
//        out.add(buffer);
        out.add(Unpooled.wrappedBuffer("Hello".getBytes()));
    }
}
