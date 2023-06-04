package com.ewan.dunjeon.world.entities.memory.celldata;

import com.ewan.dunjeon.world.entities.memory.StateData;

import java.util.List;

public class CellEntitiesStateData extends StateData {
    final List<Long> uuidsOfContainedEntities;
    public CellEntitiesStateData(double time, List<Long> uuids) {
        super(time);
        uuidsOfContainedEntities = uuids;
    }
}
