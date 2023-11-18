package com.ewan.meworking.codec;

import com.esotericsoftware.kryo.kryo5.io.Output;
import com.ewan.meworking.data.ClientData;
import com.ewan.meworking.data.ServerData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import com.esotericsoftware.kryo.kryo5.Kryo;

import java.util.List;

public class ServerDataEncoder extends MessageToMessageEncoder<ServerData> {

    Kryo kryo = KryoPreparator.getAKryo();

    @Override
    protected void encode(ChannelHandlerContext ctx, ServerData msg, List<Object> out) throws Exception {
        System.out.println("ServerDataEncoder.encode");
            ByteBuf buffer = Unpooled.buffer();
            Output output = new Output((int)buffer.maxWritableBytes());
            kryo.writeObject(output, msg);
            output.flush();
            out.add(buffer);

    }
}
