package com.ewan.dunjeon.data;

import java.util.List;

public class DataWrappers {
    public static class EntityDataWrapper extends DataWrapper<Datas.EntityData, Long> {
        public EntityDataWrapper(List<Datas.EntityData> data, Long identifier, Sensor<? extends DataStreamParameters> s) {
            super(data, identifier, s);
        }
    }
}
