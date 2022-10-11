package com.ewan.dunjeon.world.cells;

import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.entities.memory.CellMemory;
import com.ewan.dunjeon.world.entities.memory.EntityMemory;
import com.ewan.dunjeon.world.furniture.Furniture;

//TODO Change visual processing here when necessary :)
public class VisualProcessor {
    public static CellMemory.CellRenderData getVisual(BasicCell spotted, Entity spotter){
        return new CellMemory.CellRenderData(spotted);
    }

    public static CellMemory.FurnitureData.FurnitureRenderData getVisual(Furniture spotted, Entity spotter){
        return new CellMemory.FurnitureData.FurnitureRenderData(spotted);
    }

    public static EntityMemory.EntityRenderData getVisual(Entity spotted, Entity spotter){
        return new EntityMemory.EntityRenderData(spotted);
    }


}
