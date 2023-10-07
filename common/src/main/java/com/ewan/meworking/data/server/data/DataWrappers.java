package com.ewan.meworking.data.server.data;

import com.ewan.meworking.data.server.CellPosition;

import java.util.List;

/**
 * One datawrapper per abstract class extending Data.
 * Stores time, source, identifier, and all relevant data in a list.
 */
public class DataWrappers {

    public static class EntityDataWrapper extends DataWrapper<Datas.EntityData, Long> {
        public EntityDataWrapper(List<Datas.EntityData> data, Long identifier, double timestamp) {
            super(data, Datas.EntityData.class, identifier, timestamp);
        }
    }

    public static class CellDataWrapper extends DataWrapper<Datas.CellData, CellPosition> {

        public CellDataWrapper(List<Datas.CellData> data, CellPosition identifier, double timestamp) {
            super(data, Datas.CellData.class, identifier, timestamp);
        }
    }



}
