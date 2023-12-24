package com.ewan.meworking.data.server.data;

import com.ewan.meworking.data.server.CellPosition;

import java.util.List;

/**
 * One datawrapper per abstract class extending Data.
 * Stores time, source, identifier, and all relevant data in a list.
 */
public class DataWrappers {

    static private class DataWrapperImpl<D extends Data, I> extends DataWrapper<D,I>{
        private DataWrapperImpl(List<D> data, Class<D> baseClass, I identifier, double timestamp) {
            super(data, baseClass, identifier, timestamp);
        }
    }

    static private class DataWrapperImplNonGeneric extends DataWrapper{
        private DataWrapperImplNonGeneric(List<?> data, Class<?> baseClass, Object identifier, double timestamp) {
            super(data, baseClass, identifier, timestamp);
        }
    }

    public static <D extends Data, I> DataWrapper<?, ?> readFromGenericFields(List<Data> data, Class<?> baseClass, Object identifier, double timestamp){
        return new DataWrapperImplNonGeneric(data, baseClass, identifier, timestamp);
    }

    public static DataWrapper<Datas.EntityData, Long> wrapEntityData(List<Datas.EntityData> data, Long identifier, double timestamp){
        return new DataWrapperImpl<>(data, Datas.EntityData.class, identifier, timestamp);
    }

    public static DataWrapper<Datas.CellData, CellPosition> wrapCellData(List<Datas.CellData> data, CellPosition identifier, double timestamp){
        return new DataWrapperImpl<>(data, Datas.CellData.class, identifier, timestamp);
    }
//
//    public static class EntityDataWrapper extends DataWrapper<Datas.EntityData, Long> {
//        public EntityDataWrapper(List<Datas.EntityData> data, Long identifier, double timestamp) {
//            super(data, Datas.EntityData.class, identifier, timestamp);
//        }
//    }
//
//    public static class CellDataWrapper extends DataWrapper<Datas.CellData, CellPosition> {
//
//        public CellDataWrapper(List<Datas.CellData> data, CellPosition identifier, double timestamp) {
//            super(data, Datas.CellData.class, identifier, timestamp);
//        }
//    }



}
