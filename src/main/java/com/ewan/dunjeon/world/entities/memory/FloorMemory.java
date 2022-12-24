package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.world.level.Floor;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class FloorMemory{
    public FloorMemory(Floor f){
        cellMemoryArray = new CellMemory[f.getHeight()][f.getWidth()];
        entityMemories = new HashMap<>();
        soundMemories = new HashSet<>();
    }
    private final CellMemory[][] cellMemoryArray;
    private final HashMap<Long, EntityMemory> entityMemories;
    private final Set<SoundMemory> soundMemories;

    public void setAllDataToOld(){
        streamCellData().filter(Objects::nonNull).forEach(Memory::setOldData);
        entityMemories.values().forEach(Memory::setOldData);
    }

    public CellMemory getDataAt(int x, int y){
        return cellMemoryArray[y][x];
    }

    public CellMemory getDataAt(Point p){
        return cellMemoryArray[p.y][p.x];
    }

    public Stream<CellMemory> streamCellData(){
        return Arrays.stream(cellMemoryArray).flatMap(Arrays::stream);
    }

    public CellMemory getCellMemoryOfEntityMemoryLocation(EntityMemory mem){
        return getDataAt((int)Math.floor(mem.getX()), (int)Math.floor(mem.getY()));
    }

    public List<EntityMemory> getEntityMemory() {return new ArrayList<>(entityMemories.values());}
    public void updateEntity(long UUID, EntityMemory e){
        if(entityMemories.containsKey(UUID)){
            entityMemories.get(UUID).update(e);
        }else {
            entityMemories.put(UUID, e);
        }

    }
    public void updateCell(int x, int y, CellMemory d){
        CellMemory old = cellMemoryArray[y][x];
        if(old != null){
            old.update(d);
        }else{
            cellMemoryArray[y][x] = d;
        }
    }
    public EntityMemory getEntity(long UUID){ return entityMemories.get(UUID);}
    public void addSoundMemory(SoundMemory m){soundMemories.add(m);}
}
