package com.ewan.meworking.codec;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.Registration;
import com.esotericsoftware.kryo.kryo5.Serializer;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.ewan.meworking.data.ServerData;
import com.ewan.meworking.data.server.CellPosition;
import com.ewan.meworking.data.server.data.Data;
import com.ewan.meworking.data.server.data.Datas;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import com.ewan.meworking.data.server.memory.KnowledgeFragment;
import com.ewan.meworking.data.server.memory.KnowledgePackage;
import org.dyn4j.geometry.Vector2;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class KryoPreparator {

    public static class KnowledgePackageSerializer extends Serializer<KnowledgePackage<?, ?>> {

        @Override
        public void write(Kryo kryo, Output output, KnowledgePackage<?, ?> object) {
            kryo.writeObject(output, object.getIdentifier().getClass());
            kryo.writeObject(output, object);
//            kryo.writeObject(output, object.getDataMap());
        }

        @Override
        public KnowledgePackage<?, ?> read(Kryo kryo, Input input, Class<? extends KnowledgePackage<?, ?>> type) {
            Class<?> identiferClass = kryo.readObject(input, Class.class);
            Object identifier = kryo.readObject(input, identiferClass);
//            HashMap<Class<?>, KnowledgeFragment<?>> dataMap = kryo.readObject(input, HashMap.class);
            HashMap<Class<?>, KnowledgeFragment<?>> dataMap = new HashMap<>();
            KnowledgePackage<?,?> retVal = new KnowledgePackage(identifier, dataMap);
            return retVal;
        }
    }

    public static Kryo getAKryo(){
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.setReferences(true);
        kryo.register(CellPosition.class, new Serializer<CellPosition>() {
            @Override
            public void write(Kryo kryo, Output output, CellPosition o) {
                output.writeLong(o.getFloorID());
                kryo.writeObject(output, o.getPosition());
            }

            @Override
            @SuppressWarnings("unchecked")
            public CellPosition read(Kryo kryo, Input input, Class aClass) {
                long floorID = input.readLong();
                Vector2 v = kryo.readObject(input, Vector2.class);
                return new CellPosition(v, floorID);
            }
        });
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
        kryo.register(BasicMemoryBank.class, new Serializer<BasicMemoryBank>() {
            @Override
            public void write(Kryo kryo, Output output, BasicMemoryBank object) {
                output.writeLong(object.getOwnerUUID());
                kryo.writeObject(output, object.getFloorKnowledgeHashMap());
                kryo.writeObject(output, object.getCreatureKnowledgeHashMap());
                kryo.writeObject(output, object.getCellKnowledgeHashMap());

            }
            @Override
            @SuppressWarnings("unchecked")
            public BasicMemoryBank read(Kryo kryo, Input input, Class type) {
                long uuid = input.readLong();
                ConcurrentHashMap<Long, KnowledgePackage<Long, Datas.FloorData>> floorKnowledgeHashMap = kryo.readObject(input, ConcurrentHashMap.class);
                ConcurrentHashMap<Long, KnowledgePackage<Long, Datas.EntityData>> creatureKnowledgeHashMap = kryo.readObject(input, ConcurrentHashMap.class);
                ConcurrentHashMap<CellPosition, KnowledgePackage<CellPosition, Datas.CellData>> cellKnowledgeHashMap = kryo.readObject(input, ConcurrentHashMap.class);
//                ConcurrentHashMap<Long, KnowledgePackage<Long, Datas.EntityData>> creatureKnowledgeHashMap = new ConcurrentHashMap<>();
//                ConcurrentHashMap<Long, KnowledgePackage<Long, Datas.FloorData>> floorKnowledgeHashMap = new ConcurrentHashMap<>();
//                ConcurrentHashMap<CellPosition, KnowledgePackage<CellPosition, Datas.CellData>> cellKnowledgeHashMap = new ConcurrentHashMap<>();

                return new BasicMemoryBank(uuid, creatureKnowledgeHashMap, floorKnowledgeHashMap, cellKnowledgeHashMap);
            }
        });
        kryo.register(KnowledgePackage.class, new KnowledgePackageSerializer());
        kryo.register(ServerData.class, new Serializer<ServerData>() {
            @Override
            public void write(Kryo kryo, Output output, ServerData object) {
                kryo.writeObject(output, object.getBasicMemoryBank());
            }

            @Override
            public ServerData read(Kryo kryo, Input input, Class type) {
                return new ServerData(kryo.readObject(input, BasicMemoryBank.class));
            }
        });
        return kryo;
    }
}
