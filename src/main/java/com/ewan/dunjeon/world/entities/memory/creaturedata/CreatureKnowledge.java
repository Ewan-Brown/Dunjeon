package com.ewan.dunjeon.world.entities.memory.creaturedata;


import com.ewan.dunjeon.data.Datas;
import com.ewan.dunjeon.data.UpdateableData;
import com.ewan.dunjeon.world.entities.memory.Knowledge;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

/**
 * Represents basic information that is known about a Creature, from another Creature's perspective.
 */

public class CreatureKnowledge extends Knowledge<Long, Datas.EntityData> {

    HashMap<Class<? extends Datas.EntityData>, Datas.EntityData> dataMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends Datas.EntityData > void register(T object){

        if(object instanceof Datas.EntityPositionalData){
            Datas.EntityPositionalData pos = (Datas.EntityPositionalData) object;
            System.out.println("[KNOWLEDGE] Registering positional data : " + pos.getPosition());
        }

        //If this is a type of data that is updateable (and is already registered) then update it, otherwise overwrite/add it.
        Class<T> clazz = (Class<T>) object.getClass();
        if(dataMap.containsKey(clazz) && dataMap.get(clazz) instanceof UpdateableData){
            ((UpdateableData<T>) dataMap.get(clazz)).updateWithData(object);
        }else{
            dataMap.put(clazz, object);
        }

        if(object instanceof Datas.EntityPositionalData){
            System.out.println("[KNOWLEDGE] registered entity data is now : " + ((Datas.EntityPositionalData)dataMap.get(Datas.EntityPositionalData.class)).getPosition());
        }
    }

    public <T extends Datas.EntityData> T get(Class<? extends T> clazz){
        return clazz.cast(dataMap.get(clazz));
    }


    public CreatureKnowledge(Long id){
        super(id);
    }
}
