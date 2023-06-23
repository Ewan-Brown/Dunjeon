package com.ewan.dunjeon.world.entities.memory.celldata;

import com.ewan.dunjeon.data.Datas;
import com.ewan.dunjeon.data.UpdateableData;

import java.util.HashMap;

public class CellKnowledge {

    final int x;
    final int y;
    final long floorID;

    public CellKnowledge(int x, int y, long floorID){
        this.x = x;
        this.y = y;
        this.floorID = floorID;
    }

    HashMap<Class<? extends Datas.CellData>, Datas.CellData> dataMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends Datas.CellData > void register(T object){
//        if(object.getEntityUUID() != UUID){
//            throw new IllegalArgumentException("UUID of Data Object doesn't match UUID of CreatureKnowledge we're trying to attach it to");
//        }
        //If this is a type of data that is updateable (and is already registered) then update it, otherwise overwrite/add it.
        Class<T> clazz = (Class<T>) object.getClass();
        if(dataMap.containsKey(clazz) && dataMap.get(clazz) instanceof UpdateableData){
            ((UpdateableData<T>) dataMap.get(clazz)).updateWithData(object);
        }else{
            dataMap.put(clazz, object);
        }
    }

    public <T extends Datas.CellData> T get(Class<? extends T> clazz){
        return clazz.cast(dataMap.get(clazz));
    }


}
