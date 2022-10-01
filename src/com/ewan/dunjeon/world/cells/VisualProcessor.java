package com.ewan.dunjeon.world.cells;

import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.entities.memory.CellData;
import com.ewan.dunjeon.world.furniture.Furniture;

//TODO Change visual processing here when necessary :)
public class VisualProcessor {
    public static CellData.CellRenderData getVisual(BasicCell c, Entity e){
        return new CellData.CellRenderData(c);
    }

    public static CellData.FurnitureData.FurnitureRenderData getVisual(Furniture f, Entity e){
        return new CellData.FurnitureData.FurnitureRenderData(f);
    }


}
