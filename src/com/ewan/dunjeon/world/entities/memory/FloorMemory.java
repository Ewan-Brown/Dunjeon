package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.world.level.Floor;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class FloorMemory{
    public FloorMemory(Floor f){
        cellDataArray = new CellData[f.getHeight()][f.getWidth()];
        entityMemories = new HashMap<>();
    }
    private CellData[][] cellDataArray;
    private HashMap<Long, EntityMemory> entityMemories;

    public void setAllDataToOld(){
        streamData().filter(Objects::nonNull).forEach(Memory::setOldData);
    }

    public CellData getDataAt(int x,int y){
        return cellDataArray[y][x];
    }

    public Stream<CellData> streamData(){
        return Arrays.stream(cellDataArray).flatMap(x -> Arrays.stream(x));
    }

    public void updateEntity(long UUID, EntityMemory e){ entityMemories.put(UUID, e);}
    public void updateCell(int x, int y, CellData d){
        cellDataArray[y][x] = d;
    }
}
