package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.data.Data;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.entities.memory.celldata.CellKnowledge;

import java.util.ArrayList;
import java.util.List;

//TODO create a FloorData object
public class FloorKnowledge extends Knowledge<Long, Data>{
    private List<Long> lastKnownCreaturesUUIDS = new ArrayList<>();

    public FloorKnowledge(long floorID){
        super(floorID);
//        cellKnowledgeMap = new CellKnowledge[h][w];
    }

//    public CellKnowledge getCellKnowledge(int x, int y){
//        if(cellKnowledgeMap[y][x] == null){
//            cellKnowledgeMap[y][x] = new CellKnowledge(new WorldUtils.CellPosition(x, y, getIdentifier()));
//        }
//        return cellKnowledgeMap[y][x];
//    }

    @Override
    public void register(Data object) {

    }
}
