package com.ewan.dunjeon.world.cells;

import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.entities.memory.CellData;
import com.ewan.dunjeon.world.entities.memory.EntityMemory;
import com.ewan.dunjeon.world.furniture.Furniture;

//TODO Change visual processing here when necessary :)
public class VisualProcessor {
    public static CellData.CellRenderData getVisual(BasicCell spotted, Entity spotter){
        return new CellData.CellRenderData(spotted);
    }

    public static CellData.FurnitureData.FurnitureRenderData getVisual(Furniture spotted, Entity spotter){
        return new CellData.FurnitureData.FurnitureRenderData(spotted);
    }

    public static EntityMemory.EntityRenderData getVisual(Entity spotted, Entity spotter){
        return new EntityMemory.EntityRenderData(spotted);
    }


}
