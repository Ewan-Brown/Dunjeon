package com.ewan.dunjeon.world.cells;

import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.entities.memory.CellData;
import com.ewan.dunjeon.world.furniture.Furniture;

public class VisualProcessor {
    //TODO Change visual processing here when necessary :)
    public static CellData.CellVisualData getVisual(BasicCell c, Entity e){
        return new CellData.CellVisualData(c);
    }

    public static CellData.FurnitureData.FurnitureVisualData getVisual(BasicCell c, Furniture f){
        return new CellData.FurnitureData.FurnitureVisualData(f);
    }
}
