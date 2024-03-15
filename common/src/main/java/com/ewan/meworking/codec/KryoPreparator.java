package com.ewan.meworking.codec;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.Serializer;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.ewan.meworking.data.client.ClientInputData;
import com.ewan.meworking.data.server.DataPacket;
import com.ewan.meworking.data.client.UserInput;
import com.ewan.meworking.data.server.data.CellPosition;
import com.ewan.meworking.data.server.data.Data;
import com.ewan.meworking.data.server.data.DataWrapper;
import com.ewan.meworking.data.server.data.DataWrappers;
import com.ewan.meworking.data.server.metadata.FrameInfoPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.geometry.Vector2;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class KryoPreparator {

    static private final DataSerializer dataSerializer = new DataSerializer();
    static private final ClientInputSerializer clientInputSerializer = new ClientInputSerializer();
    static private final DataWrapperSerializer dataWrapperSerializer = new DataWrapperSerializer();
    static private Logger logger = LogManager.getLogger();

    public static class DataSerializer extends Serializer<Data>{
        @Override
        public void write(Kryo kryo, Output output, Data object) {
            try {
                kryo.writeObject(output, object.getClass());
            }catch(Exception e){
                throw new RuntimeException(e);
            }
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    kryo.writeClassAndObject(output, field.get(object));
                }catch(IllegalAccessException e){
                    throw new RuntimeException(e);
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
                    throw new RuntimeException(e);
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
            output.writeInt(object.getTickstamp());
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
            int tick = input.readInt();
            return DataWrappers.readFromGenericFields(datas, baseClass, o, t, tick);
        }
    }

    public static Kryo getAKryo(){
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.setReferences(true);
        kryo.register(FrameInfoPacket.class, new Serializer<FrameInfoPacket>() {
            @Override
            public void write(Kryo kryo, Output output, FrameInfoPacket object) {
                output.writeLong(object.clientUUID());
                output.writeDouble(object.worldTimeExact());
                output.writeInt(object.worldTimeTicks());
                output.writeInt(object.expectedDataCount());
            }
            @Override
            public FrameInfoPacket read(Kryo kryo, Input input, Class<? extends FrameInfoPacket> type) {
                return new FrameInfoPacket(input.readLong(), input.readDouble(), input.readInt(), input.readInt());
            }
        });
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
        kryo.register(DataPacket.class, new Serializer<DataPacket>() {
            @Override
            public void write(Kryo kryo, Output output, DataPacket object) {
                kryo.writeObject(output, object.getDataWrapper(), dataWrapperSerializer);
                output.flush();
            }

            @Override
            public DataPacket read(Kryo kryo, Input input, Class type) {
                DataWrapper<?,?> dataWrapper = kryo.readObject(input, DataWrapper.class, dataWrapperSerializer);
                return new DataPacket(dataWrapper);
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
