package com.ewan.dunjeon.server;

import com.esotericsoftware.kryo.kryo5.Registration;
import com.esotericsoftware.kryo.kryo5.Serializer;
import com.esotericsoftware.kryo.kryo5.SerializerFactory;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.esotericsoftware.kryo.kryo5.minlog.Log;
import com.esotericsoftware.kryo.kryo5.objenesis.instantiator.ObjectInstantiator;
import com.esotericsoftware.kryo.kryo5.objenesis.strategy.InstantiatorStrategy;
import com.esotericsoftware.kryo.kryo5.serializers.DefaultArraySerializers;
import com.ewan.dunjeon.data.Data;
import com.ewan.dunjeon.data.Sensor;
import com.ewan.dunjeon.server.world.Dunjeon;
import com.ewan.dunjeon.server.world.WorldUtils;
import com.ewan.dunjeon.server.world.entities.creatures.BasicMemoryBank;
import com.esotericsoftware.kryo.kryo5.Kryo;
import com.ewan.dunjeon.server.world.entities.memory.FloorKnowledge;
import com.ewan.dunjeon.server.world.entities.memory.KnowledgeFragment;
import com.ewan.dunjeon.server.world.entities.memory.KnowledgePackage;
import com.ewan.dunjeon.server.world.entities.memory.celldata.CellKnowledge;
import com.ewan.dunjeon.server.world.entities.memory.creaturedata.CreatureKnowledge;
import com.ewan.dunjeon.server.world.floor.Floor;
import org.dyn4j.geometry.Vector2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import static com.esotericsoftware.kryo.kryo5.Kryo.NULL;

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
        kryo.register(WorldUtils.CellPosition.class, new Serializer<WorldUtils.CellPosition>() {
            public void write(Kryo kryo, Output output, WorldUtils.CellPosition cellPos) {
                output.writeLong(cellPos.getFloorID());
                kryo.writeObject(output, cellPos.getPosition());
            }

            public WorldUtils.CellPosition read(Kryo kryo, Input input, Class<? extends WorldUtils.CellPosition> type) {
                long floorID = input.readLong();
                Vector2 vector = kryo.readObject(input, Vector2.class);
                return new WorldUtils.CellPosition(vector, floorID);
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
