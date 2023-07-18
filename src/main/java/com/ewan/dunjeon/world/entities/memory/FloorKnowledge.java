package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.data.Data;

import java.util.ArrayList;
import java.util.List;

public class FloorKnowledge extends KnowledgePackage<Long, Data> {
    private List<Long> lastKnownCreaturesUUIDS = new ArrayList<>();

    public FloorKnowledge(long floorID){
        super(floorID);
    }

}
