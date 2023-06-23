package com.ewan.dunjeon.world.entities.memory.creaturedata;


import com.ewan.dunjeon.data.Datas;
import com.ewan.dunjeon.data.UpdateableData;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

/**
 * Represents basic information that is known about a Creature, from another Creature's perspective.
 */

public class CreatureKnowledge {
    final long UUID;

    HashMap<Class<? extends Datas.EntityData>, Datas.EntityData> dataMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends Datas.EntityData > void register(T object){

        //If this is a type of data that is updateable (and is already registered) then update it, otherwise overwrite/add it.
        Class<T> clazz = (Class<T>) object.getClass();
        if(dataMap.containsKey(clazz) && dataMap.get(clazz) instanceof UpdateableData){
            ((UpdateableData<T>) dataMap.get(clazz)).updateWithData(object);
        }else{
            dataMap.put(clazz, object);
        }
    }

    public <T extends Datas.EntityData> T get(Class<? extends T> clazz){
        return clazz.cast(dataMap.get(clazz));
    }


    public CreatureKnowledge(long id){
        UUID = id;
    }
}
