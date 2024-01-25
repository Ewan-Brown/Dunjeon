package com.ewan.meworking.data.server.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * One datawrapper per abstract class extending Data.
 * Stores time, source, identifier, and all relevant data in a list.
 */
public class DataWrappers {

    static Logger logger = LogManager.getLogger();

    static private class DataWrapperImpl<D extends Data, I> extends DataWrapper<D,I>{
        private DataWrapperImpl(List<D> data, Class<D> baseClass, I identifier, double timestamp, int tickstamp) {
            super(data, baseClass, identifier, timestamp, tickstamp);
        }
    }

    /**
     * Used for networking purposes only
     */
    static private class DataWrapperImplNonGeneric extends DataWrapper{
        private DataWrapperImplNonGeneric(List<?> data, Class<?> baseClass, Object identifier, double timestamp, int tickstamp) {
            super(data, baseClass, identifier, timestamp, tickstamp);
        }
    }

    public static <D extends Data, I> DataWrapper<?, ?> readFromGenericFields(List<Data> data, Class<?> baseClass, Object identifier, double timestamp, int tickstamp){
        return new DataWrapperImplNonGeneric(data, baseClass, identifier, timestamp, tickstamp);
    }

    public static DataWrapper<Datas.EntityData, Long> wrapEntityData(List<Datas.EntityData> data, Long identifier, double timestamp, int tickstamp){
        return new DataWrapperImpl<>(data, Datas.EntityData.class, identifier, timestamp, tickstamp);
    }

    public static DataWrapper<Datas.CellData, CellPosition> wrapCellData(List<Datas.CellData> data, CellPosition identifier, double timestamp, int tickstamp){
        return new DataWrapperImpl<>(data, Datas.CellData.class, identifier, timestamp, tickstamp);
    }
}
