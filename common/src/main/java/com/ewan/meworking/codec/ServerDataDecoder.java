package com.ewan.meworking.codec;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.Serializer;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.ewan.dunjeon.server.world.CellPosition;
import com.ewan.meworking.data.ServerData;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.dyn4j.geometry.Vector2;

import java.util.List;

public class ServerDataDecoder
        extends ByteToMessageDecoder{

    private final Kryo kryo;

    public ServerDataDecoder(Kryo kryo) {
        this.kryo = kryo;
        initializeKryo();
    }

    private void initializeKryo(){
        kryo.setRegistrationRequired(false); //TODO This is easier but performance hit at runtime!
        kryo.setReferences(true);
//        kryo.register(BasicMemoryBank.class, new Serializer(){
//
//            @Override
//            public void write(Kryo kryo, Output output, Object cellPos) {
////                output.writeLong(cellPos.getFloorID());
////                kryo.writeObject(output, cellPos.getPosition());
//            }
//
//            public BasicMemoryBank read(Kryo kryo, Input input, Class type) {
////                long floorID = input.readLong();
////                Vector2 vector = kryo.readObject(input, Vector2.class);
////                return null;
////                return new CellPosition(vector, floorID);
//                return null;
//            }
//
//        });

        kryo.register(Vector2.class, new Serializer<Vector2>() {
            public void write(Kryo kryo, Output output, Vector2 cellPos) {
                output.writeDouble(cellPos.x);
                output.writeDouble(cellPos.y);
            }

            public Vector2 read(Kryo kryo, Input input, Class<? extends Vector2> type) {
                double x = input.readDouble();
                double y = input.readDouble();
                return new Vector2(x, y);
            }

        });
        kryo.register(CellPosition.class, new Serializer<CellPosition>() {
            public void write(Kryo kryo, Output output, CellPosition cellPos) {
                output.writeLong(cellPos.getFloorID());
                kryo.writeClassAndObject(output, cellPos.getPosition());
                output.flush();
            }

            public CellPosition read(Kryo kryo, Input input, Class<? extends CellPosition> type) {
                long floorID = input.readLong();
                Vector2 vector = (Vector2) kryo.readClassAndObject(input);
                return new CellPosition(vector, floorID);
            }

        });
    }

    @Override
    protected void decode(ChannelHandlerContext ctx,
                          ByteBuf in, List<Object> out) {

        if (in.readableBytes() < 2)
            return;

        in.markReaderIndex();

        int len = in.readUnsignedShort();

        if (in.readableBytes() < len) {
            in.resetReaderIndex();
            return;
        }

        byte[] buf = new byte[len];
        in.readBytes(buf);
        Input input = new Input(buf);
        Object object = kryo.readClassAndObject(input);
        out.add(object);
    }
}