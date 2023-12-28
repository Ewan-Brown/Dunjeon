package com.ewan.meworking.codec;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.Serializer;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.ewan.meworking.data.ClientInputData;
import com.ewan.meworking.data.ServerData;
import com.ewan.meworking.data.client.UserInput;
import com.ewan.meworking.data.server.CellPosition;
import com.ewan.meworking.data.server.data.Data;
import com.ewan.meworking.data.server.data.DataWrapper;
import com.ewan.meworking.data.server.data.DataWrappers;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import com.ewan.meworking.data.server.memory.KnowledgeFragment;
import com.ewan.meworking.data.server.memory.KnowledgePackage;
import org.dyn4j.geometry.Vector2;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class KryoPreparator {

    static DataSerializer dataSerializer = new DataSerializer();
//    static KnowledgePackageSerializer knowledgePackageSerializer = new KnowledgePackageSerializer();
//    static KnowledgeFragmentSerializer knowledgeFragmentSerializer = new KnowledgeFragmentSerializer();
    static ClientInputSerializer clientInputSerializer = new ClientInputSerializer();
    static DataWrapperSerializer dataWrapperSerializer = new DataWrapperSerializer();
    //TODO Can probably just use .write/readClassAndObject here for these non-concrete class serializers
    // https://stackoverflow.com/questions/52337925/kryo-difference-between-readclassandobject-readobject-and-writeclassandobject-w
    public static class DataSerializer extends Serializer<Data>{

        @Override
        public void write(Kryo kryo, Output output, Data object) {
            try {
                kryo.writeObject(output, object.getClass());
            }catch(Exception e){
                e.printStackTrace();
            }
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    kryo.writeClassAndObject(output, field.get(object));
                }catch(IllegalAccessException e){
                    e.printStackTrace();
                }
            }
            output.flush();
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

//    public static class KnowledgeFragmentSerializer extends Serializer<KnowledgeFragment<? extends Data>>{
//
//        @Override
//        public void write(Kryo kryo, Output output, KnowledgeFragment<? extends Data> object) {
//            System.out.println("KnowledgeFragmentSerializer.write");
//            kryo.writeObject(output, object.getInfo(), dataSerializer);
//            output.writeDouble(object.getTimestamp());
//            output.flush();
//            //TODO We DO NOT pass the source on for now... Could be useful to convert it into a less entangled format for a client though
//        }
//
//        @Override
//        public KnowledgeFragment<? extends Data> read(Kryo kryo, Input input, Class<? extends KnowledgeFragment<? extends Data>> type) {
//            System.out.println("KnowledgeFragmentSerializer.read");
//            Data info = kryo.readObject(input, Data.class, dataSerializer);
//            double timestamp = input.readDouble();
//            return new KnowledgeFragment<>(info, null, timestamp);
//        }
//    }

    public static class ClientInputSerializer extends Serializer<UserInput>{
        @Override
        public void write(Kryo kryo, Output output, UserInput object) {
            kryo.writeObject(output, object.getClass());
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    kryo.writeClassAndObject(output, field.get(object));
                }catch(IllegalAccessException e){
                    e.printStackTrace();
                }
            }
            output.flush();
        }
        ;
        @Override
        public UserInput read(Kryo kryo, Input input, Class<? extends UserInput> type) {
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
                    return (UserInput) constructor.newInstance(obj);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

//    public static class KnowledgePackageSerializer extends Serializer<KnowledgePackage<?, ?>> {
//
//        @Override
//        public void write(Kryo kryo, Output output, KnowledgePackage<?, ?> object) {
//            System.out.println("KnowledgePackageSerializer.write");
//            kryo.writeClassAndObject(output, object.getIdentifier());
//
//            output.writeInt(object.getDataMap().size());
//            for (KnowledgeFragment<?> value : object.getDataMap().values()) {
//                kryo.writeObject(output, value, knowledgeFragmentSerializer);
//            }
//            output.flush();
//        }
//
//        @Override
//        public KnowledgePackage<?, ?> read(Kryo kryo, Input input, Class<? extends KnowledgePackage<?, ?>> type) {
//            System.out.println("KnowledgePackageSerializer.read");
//            Object identifier = kryo.readClassAndObject(input);
//
//            int mapSize = input.readInt();
//            HashMap<Class<?>, KnowledgeFragment<?>> dataMap = new HashMap<>();
//            for (int i = 0; i < mapSize; i++) {
//                KnowledgeFragment<?> fragment = (KnowledgeFragment<?>) kryo.readObject(input, KnowledgeFragment.class, knowledgeFragmentSerializer);
//                dataMap.put(fragment.getInfo().getClass(), fragment);
//            }
//            KnowledgePackage<?,?> retVal = new KnowledgePackage(identifier, dataMap);
//            return retVal;
//        }
//    }

    public static class DataWrapperSerializer extends Serializer<DataWrapper<?, ?>> {

        @Override
        public void write(Kryo kryo, Output output, DataWrapper<?, ?> object) {
            output.writeInt(object.getData().size());
            for (Data datum : object.getData()) {
                kryo.writeObject(output, datum, dataSerializer);
            }
            kryo.writeObject(output, object.getBaseClass());
            kryo.writeClassAndObject(output, object.getIdentifier());
            output.writeDouble(object.getTimestamp());
            output.flush();
        }

        @Override
        public DataWrapper<?, ?> read(Kryo kryo, Input input, Class<? extends DataWrapper<?, ?>> type) {
            int dataCount = input.readInt();
            List<Data> datas = new ArrayList<>();
            for (int i = 0; i < dataCount; i++) {
                datas.add(kryo.readObject(input, Data.class, dataSerializer));
            }
            Class<?> baseClass = kryo.readObject(input, Class.class);
            Object o = kryo.readClassAndObject(input);
            double t = input.readDouble();
            return DataWrappers.readFromGenericFields(datas, baseClass, o, t);
        }
    }

    public static Kryo getAKryo(){
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.setReferences(true);
        kryo.register(CellPosition.class, new Serializer<CellPosition>() {
            @Override
            public void write(Kryo kryo, Output output, CellPosition object) {
                output.writeLong(object.getFloorID());
                kryo.writeObject(output, object.getPosition());
                output.flush();
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
                output.writeDouble(object.x);
                output.writeDouble(object.y);
                output.flush();
            }

            public Vector2 read(Kryo kryo, Input input, Class<? extends Vector2> type) {
                double x = input.readDouble();
                double y = input.readDouble();
                return new Vector2(x, y);
            }
        });
//        kryo.register(BasicMemoryBank.class, new Serializer<BasicMemoryBank>() {
//            @Override
//            public void write(Kryo kryo, Output output, BasicMemoryBank object) {
//                System.out.println("BasicMemoryBank.write");
//                output.writeLong(object.getOwnerUUID());
//                output.writeInt(object.getKnowledgeDataPairings().size());
//
//                for (BasicMemoryBank.Pairing<?,? extends Data,? extends KnowledgePackage<?,?>> knowledgeDataPairing : object.getKnowledgeDataPairings()) {
//                    output.writeInt(knowledgeDataPairing.knowledgeMap().size());
//                    kryo.writeObject(output, knowledgeDataPairing.relatedBaseDataClass());
//                    for (KnowledgePackage<?, ? extends Data> value : knowledgeDataPairing.knowledgeMap().values()) {
//                        kryo.writeObject(output, value, knowledgePackageSerializer);
//                    }
//                }
//                output.flush();
//            }
//
//            public <I, D extends Data> void readPairingIntoMap(Kryo kryo, Input input, List<BasicMemoryBank.Pairing<?, ?, ?>> pairings){
//                ConcurrentHashMap<I, KnowledgePackage<I, D>> currentKnowledgePackageMap = new ConcurrentHashMap<>();
//                int knowledgePackages = input.readInt();
//                Class<D> baseDataClazz = kryo.readObject(input, Class.class);
//
//                for (int j = 0; j < knowledgePackages; j++) {
//                    KnowledgePackage<I, D> kPackage = kryo.readObject(input, KnowledgePackage.class, knowledgePackageSerializer);
//                    currentKnowledgePackageMap.put(kPackage.getIdentifier(), kPackage);
//                }
//                BasicMemoryBank.Pairing<I, D, KnowledgePackage<I, D>> p = new BasicMemoryBank.Pairing<>(currentKnowledgePackageMap, baseDataClazz);
//                pairings.add(p);
//            }
//
//            @Override
//            @SuppressWarnings("unchecked")
//            public BasicMemoryBank read(Kryo kryo, Input input, Class type) {
//                System.out.println("BasicMemoryBank.read");
//                long uuid = input.readLong();
//                int knowledgeDataPairings = input.readInt();
//
//                List<BasicMemoryBank.Pairing<?, ?, ?>> pairings = new ArrayList<>();
//
//                for (int i = 0; i < knowledgeDataPairings; i++) {
//                    readPairingIntoMap(kryo, input, pairings);
//                }
//
//                return new BasicMemoryBank(uuid, pairings);
//            }
//        });
        kryo.register(ServerData.class, new Serializer<ServerData>() {
            @Override
            public void write(Kryo kryo, Output output, ServerData object) {
                output.writeDouble(object.getWorldTime());
                int dataCount = object.getDataWrappers().size();
                output.writeInt(dataCount);
                for (DataWrapper<? extends Data, ?> dataWrapper : object.getDataWrappers()) {
                    kryo.writeObject(output, dataWrapper, dataWrapperSerializer);
                }
                output.flush();
            }

            @Override
            public ServerData read(Kryo kryo, Input input, Class type) {
                double timestamp = input.readDouble();
                int dataWrapperCount = input.readInt();
                List<DataWrapper<?,?>> dataWrappers = new ArrayList<>();
                for (int i = 0; i < dataWrapperCount; i++) {
                    dataWrappers.add(kryo.readObject(input, DataWrapper.class, dataWrapperSerializer));
                }
                return new ServerData(dataWrappers, timestamp);
            }
        });
        kryo.register(ClientInputData.class, new Serializer<ClientInputData>() {
            @Override
            public void write(Kryo kryo, Output output, ClientInputData object) {
                output.writeInt(object.inputs().size());
                for (UserInput input : object.inputs()) {
                    kryo.writeObject(output, input, clientInputSerializer);
                }
                output.flush();
            }
            @Override
            public ClientInputData read(Kryo kryo, Input input, Class type) {
                List<UserInput> actions = new ArrayList<>();
                int actionCount = input.readInt();
                for (int i = 0; i < actionCount; i++) {
                    actions.add(kryo.readObject(input, UserInput.class ,clientInputSerializer));
                }
                return new ClientInputData(actions);
            }
        });
        return kryo;
    }
}
