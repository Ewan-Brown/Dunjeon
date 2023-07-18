package com.ewan.dunjeon.data;

import com.ewan.dunjeon.world.WorldUtils;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * One datawrapper per abstract class extending Data.
 * Stores time, source, identifier, and all relevant data in a list.
 */
public class DataWrappers {

    public static class EntityDataWrapper extends DataWrapper<Datas.EntityData, Long> {
        public EntityDataWrapper(List<Datas.EntityData> data, Long identifier, Sensor<? extends DataStreamParameters> sourceSensor, double timestamp) {
            super(data, identifier, sourceSensor, timestamp);
        }
    }

    public static class CellDataWrapper extends DataWrapper<Datas.CellData, WorldUtils.CellPosition> {

        public CellDataWrapper(List<Datas.CellData> data, WorldUtils.CellPosition identifier, Sensor<? extends DataStreamParameters> sourceSensor, double timestamp) {
            super(data, identifier, sourceSensor, timestamp);
        }
    }



}
