package com.ewan.meworking.codec;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.Serializer;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.ewan.dunjeon.server.world.CellPosition;
import com.ewan.meworking.data.ServerData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.dyn4j.geometry.Vector2;

import java.io.ByteArrayOutputStream;

public class ServerDataEncoder
        extends MessageToByteEncoder<ServerData> {

    private final Kryo kryo;

    public ServerDataEncoder(Kryo kryo) {
        this.kryo = kryo;
        initializeKryo();
    }


    private void initializeKryo(){
        kryo.setRegistrationRequired(false); //TODO This is easier but performance hit at runtime!
        kryo.setReferences(true);

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
    protected void encode(ChannelHandlerContext ctx,
                          ServerData msg, ByteBuf out) {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        Output output = new Output(outStream, 4096);

        kryo.writeClassAndObject(output, msg);

        byte[] outArray = outStream.toByteArray();
        out.writeShort(outArray.length);
        out.writeBytes(outArray);
        output.flush();
    }
}