package com.ewan.dunjeon.data;

import com.ewan.dunjeon.world.WorldUtils;

import java.util.List;

public class DataWrappers {

    public static class EntityDataWrapper extends DataWrapper<Datas.EntityData, Long> {
        public EntityDataWrapper(List<Datas.EntityData> data, Long identifier, Sensor<? extends DataStreamParameters> s) {
            super(data, identifier, s);
        }
    }

    public static class CellDataWrapper extends DataWrapper<Datas.CellData, WorldUtils.CellPosition> {
        public CellDataWrapper(List<Datas.CellData> data, WorldUtils.CellPosition identifier, Sensor<? extends DataStreamParameters> s) {
            super(data, identifier, s);
        }

    }



}
