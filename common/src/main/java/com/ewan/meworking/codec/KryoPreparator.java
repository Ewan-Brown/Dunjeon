package com.ewan.meworking.codec;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.Registration;
import com.esotericsoftware.kryo.kryo5.Serializer;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.esotericsoftware.kryo.kryo5.minlog.Log;
import com.esotericsoftware.kryo.kryo5.serializers.DefaultArraySerializers;
import com.ewan.meworking.data.ServerData;
import com.ewan.meworking.data.server.CellPosition;
import com.ewan.meworking.data.server.data.Data;
import com.ewan.meworking.data.server.data.Datas;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import com.ewan.meworking.data.server.memory.KnowledgeFragment;
import com.ewan.meworking.data.server.memory.KnowledgePackage;
import org.dyn4j.geometry.Vector2;

import java.io.Serial;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KryoPreparator {

    static DataSerializer dataSerializer = new DataSerializer();
    static KnowledgePackageSerializer knowledgePackageSerializer = new KnowledgePackageSerializer();
    static KnowledgeFragmentSerializer knowledgeFragmentSerializer = new KnowledgeFragmentSerializer();

    //TODO Can probably just use .write/readClassAndObject here for these non-concrete class serializers
    // https://stackoverflow.com/questions/52337925/kryo-difference-between-readclassandobject-readobject-and-writeclassandobject-w
    public static class DataSerializer extends Serializer<Data>{

        @Override
        public void write(Kryo kryo, Output output, Data object) {
            kryo.writeObject(output, object.getClass());
            System.out.println("Writing: " + object.getClass());
            Field[] fields = object.getClass().getDeclaredFields();
            System.out.println("WRITING FIELDS!");
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    System.out.println(field.get(object));
                    kryo.writeClassAndObject(output, field.get(object));
                }catch(IllegalAccessException e){
                    e.printStackTrace();
                }
            }
        }

        @Override
        public Data read(Kryo kryo, Input input, Class<? extends Data> type) {
            Class<?> clazz = kryo.readObject(input, Class.class);
            Field[] fields = clazz.getDeclaredFields();
            Object[] obj = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                obj[i] = kryo.readClassAndObject(input);
            }

            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            if(constructors.length != 1){
                throw new IllegalArgumentException("This data class should have EXACTLY 1 all-arg constructor! : " + clazz.toString());
            }else{
                Constructor<?> constructor = constructors[0];
                try {
                    return (Data) constructor.newInstance(obj);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

    public static class KnowledgeFragmentSerializer extends Serializer<KnowledgeFragment<? extends Data>>{

        @Override
        public void write(Kryo kryo, Output output, KnowledgeFragment<? extends Data> object) {
            kryo.writeObject(output, object.getInfo(), dataSerializer);
            output.writeDouble(object.getTimestamp());
            //TODO We DO NOT pass the source on for now... Could be useful to convert it into a less entangled format for a client though
        }

        @Override
        public KnowledgeFragment<? extends Data> read(Kryo kryo, Input input, Class<? extends KnowledgeFragment<? extends Data>> type) {
            Data info = kryo.readObject(input, Data.class, dataSerializer);
            double timestamp = input.readDouble();
            return new KnowledgeFragment<>(info, null, timestamp);
        }
    }
    public static class KnowledgePackageSerializer extends Serializer<KnowledgePackage<?, ?>> {

        @Override
        public void write(Kryo kryo, Output output, KnowledgePackage<?, ?> object) {
            System.out.println("Writing: " + object.getClass());
            kryo.writeClassAndObject(output, object.getIdentifier());

            output.writeInt(object.getDataMap().size());
            for (KnowledgeFragment<?> value : object.getDataMap().values()) {
                kryo.writeObject(output, value, knowledgeFragmentSerializer);
            }
        }

        @Override
        public KnowledgePackage<?, ?> read(Kryo kryo, Input input, Class<? extends KnowledgePackage<?, ?>> type) {
//            Class<?> identiferClass = kryo.readObject(input, Class.class);
            Object identifier = kryo.readClassAndObject(input);

            int mapSize = input.readInt();
            HashMap<Class<?>, KnowledgeFragment<?>> dataMap = new HashMap<>();
            for (int i = 0; i < mapSize; i++) {
                KnowledgeFragment<?> fragment = (KnowledgeFragment<?>) kryo.readObject(input, KnowledgeFragment.class, knowledgeFragmentSerializer);
                dataMap.put(fragment.getInfo().getClass(), fragment);
            }
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
            public void write(Kryo kryo, Output output, CellPosition object) {
                System.out.println("Writing: " + object.getClass());
                output.writeLong(object.getFloorID());
                kryo.writeObject(output, object.getPosition());
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
            public void write(Kryo kryo, Output output, Vector2 object) {
                System.out.println("Writing: " + object.getClass());
                output.writeDouble(object.x);
                output.writeDouble(object.y);
            }

            public Vector2 read(Kryo kryo, Input input, Class<? extends Vector2> type) {
                double x = input.readDouble();
                double y = input.readDouble();
                return new Vector2(x, y);
            }
        });
//        kryo.addDefaultSerializer(KnowledgePackage.class, knowledgeFragmentSerializer);
        kryo.register(BasicMemoryBank.class, new Serializer<BasicMemoryBank>() {
            @Override
            public void write(Kryo kryo, Output output, BasicMemoryBank object) {
                System.out.println("Writing: " + object.getClass());
                output.writeLong(object.getOwnerUUID());

                output.writeInt(object.getFloorKnowledgeHashMap().size());
                output.writeInt(object.getCreatureKnowledgeHashMap().size());
                output.writeInt(object.getCellKnowledgeHashMap().size());

                for (KnowledgePackage<Long, Datas.FloorData> value : object.getFloorKnowledgeHashMap().values()) {
                    kryo.writeObject(output, value, knowledgePackageSerializer);

                }
                for (KnowledgePackage<Long, Datas.EntityData> value : object.getCreatureKnowledgeHashMap().values()) {
                    kryo.writeObject(output, value, knowledgePackageSerializer);
                }
                for (KnowledgePackage<CellPosition, Datas.CellData> value : object.getCellKnowledgeHashMap().values()) {
                    kryo.writeObject(output, value, knowledgePackageSerializer);
                }

            }
            @Override
            @SuppressWarnings("unchecked")
            public BasicMemoryBank read(Kryo kryo, Input input, Class type) {
                long uuid = input.readLong();


                int floorMapSize = input.readInt();
                int creatureMapSize = input.readInt();
                int cellMapSize = input.readInt();

                ConcurrentHashMap<Long, KnowledgePackage<Long, Datas.FloorData>> floorKnowledgeHashMap = new ConcurrentHashMap<>(floorMapSize);
                ConcurrentHashMap<Long, KnowledgePackage<Long, Datas.EntityData>> creatureKnowledgeHashMap = new ConcurrentHashMap<>(creatureMapSize);
                ConcurrentHashMap<CellPosition, KnowledgePackage<CellPosition, Datas.CellData>> cellKnowledgeHashMap = new ConcurrentHashMap<>(cellMapSize);

                for (int i = 0; i < floorMapSize; i++) {
                    KnowledgePackage<Long, Datas.FloorData> kPackage = kryo.readObject(input, KnowledgePackage.class, knowledgePackageSerializer);
                    floorKnowledgeHashMap.put(kPackage.getIdentifier(), kPackage);
                }

                for (int i = 0; i < creatureMapSize; i++) {
                    KnowledgePackage<Long, Datas.EntityData> kPackage = kryo.readObject(input, KnowledgePackage.class, knowledgePackageSerializer);
                    creatureKnowledgeHashMap.put(kPackage.getIdentifier(), kPackage);
                }

                for (int i = 0; i < cellMapSize; i++) {
                    KnowledgePackage<CellPosition, Datas.CellData> kPackage = kryo.readObject(input, KnowledgePackage.class, knowledgePackageSerializer);
                    cellKnowledgeHashMap.put(kPackage.getIdentifier(), kPackage);
                }

                return new BasicMemoryBank(uuid, creatureKnowledgeHashMap, floorKnowledgeHashMap, cellKnowledgeHashMap);
            }
        });
        kryo.register(ServerData.class, new Serializer<ServerData>() {
            @Override
            public void write(Kryo kryo, Output output, ServerData object) {
                System.out.println("Writing: " + object.getClass());
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
