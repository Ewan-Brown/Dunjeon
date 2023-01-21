package com.ewan.dunjeon.world.cells;

import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.entities.memory.CellMemory;
import com.ewan.dunjeon.world.entities.memory.EntityMemory;
import com.ewan.dunjeon.world.furniture.Furniture;

import java.util.List;

//TODO Change visual processing here when necessary :)
public class VisualProcessor {
    {
        System.err.println("Remove this stupid visual processor class... I don't think this should exist for now");
    }
    public static CellMemory.CellRenderData getVisual(BasicCell spotted, Entity spotter, List<WorldUtils.Side> visibleSides){
        return new CellMemory.CellRenderData(spotted, visibleSides, spotted.isFilled());
    }
    public static CellMemory.FurnitureData.FurnitureRenderData getVisual(Furniture spotted, Entity spotter){
        return new CellMemory.FurnitureData.FurnitureRenderData(spotted);
    }


}
