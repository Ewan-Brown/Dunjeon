package com.ewan.dunjeon.world.entities.memory.celldata;

import com.ewan.dunjeon.data.Datas;
import com.ewan.dunjeon.data.UpdateableData;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.entities.memory.Knowledge;

import java.util.HashMap;

public class CellKnowledge extends Knowledge<WorldUtils.CellPosition, Datas.CellData> {


    public CellKnowledge(WorldUtils.CellPosition identifier){
        super(identifier);
    }

    HashMap<Class<? extends Datas.CellData>, Datas.CellData> dataMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends Datas.CellData > void register(T object){
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
