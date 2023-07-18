package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.data.Data;
import com.ewan.dunjeon.data.Datas;
import lombok.Getter;

import java.util.HashMap;

public abstract class KnowledgePackage<I, D extends Data> {
    @Getter
    private final I identifier;
    public KnowledgePackage(I identifier) {
        this.identifier = identifier;
    }

    HashMap<Class<? extends D>, KnowledgeFragment<D>> dataMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends KnowledgeFragment<D>> void register(T object){

        Class<? extends D> clazz = (Class<? extends D>) object.info.getClass();
        dataMap.put(clazz, object);

    }

    public <T extends D> T get(Class<T> clazz){
        return clazz.cast(dataMap.get(clazz).info);
    }

}

