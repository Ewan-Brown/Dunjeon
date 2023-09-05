package com.ewan.dunjeon.world.entities.creatures;

import java.util.Objects;

public class CreatureUtils {

//    public static long countUnexploredVisibleCells(Creature creature){
//        return creature.getCurrentFloorMemory().streamCellData().filter(cellMemory -> !Objects.isNull(cellMemory)
//                && cellMemory.getExploredStatus() != CellMemory.ExploredStatus.EXPLORED_UNCHANGED
//                && !cellMemory.isOldData()
//                && cellMemory.enterable == CellMemory.EnterableStatus.OPEN).count();
//    }
//
//    public static long countUnexploredCells(Creature creature){
//        return creature.getCurrentFloorMemory().streamCellData().filter(cellMemory -> !Objects.isNull(cellMemory)
//                && cellMemory.getExploredStatus() != CellMemory.ExploredStatus.EXPLORED_UNCHANGED
//                && cellMemory.enterable == CellMemory.EnterableStatus.OPEN).count();
//    }
//
//    public static boolean isCellCurrentlyVisible(Creature c, int x,int y){
//        CellMemory mem = c.getCurrentFloorMemory().getDataAt(x, y);
//        return mem != null && !mem.isOldData();
//    }
}
