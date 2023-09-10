package com.ewan.dunjeon.server;

import com.esotericsoftware.kryo.kryo5.Serializer;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.ewan.dunjeon.server.world.CellPosition;
import com.ewan.dunjeon.server.world.Dunjeon;
import com.ewan.dunjeoncommon.memory.BasicMemoryBank;
import com.esotericsoftware.kryo.kryo5.Kryo;
import org.dyn4j.geometry.Vector2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Interface between packets and server. Send and receive.
 */
public class ServerManager {

    public static void SerializePlayerData(){
        Kryo kryo = new Kryo();
        Dunjeon dunjeon = Dunjeon.getInstance();
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
                kryo.writeObject(output, cellPos.getPosition());
            }

            public CellPosition read(Kryo kryo, Input input, Class<? extends CellPosition> type) {
                long floorID = input.readLong();
                Vector2 vector = kryo.readObject(input, Vector2.class);
                return new CellPosition(vector, floorID);
            }

        });
        BasicMemoryBank memoryBank = dunjeon.getPlayer().getMemoryBank();
        try {
            Output output = new Output(new FileOutputStream("file.bin"));
            kryo.writeObject(output, memoryBank);
            output.close();

            Input input = new Input(new FileInputStream("file.bin"));
            BasicMemoryBank memoryBank2 = kryo.readObject(input, BasicMemoryBank.class);
            input.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
