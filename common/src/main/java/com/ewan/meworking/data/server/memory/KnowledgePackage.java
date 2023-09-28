package com.ewan.meworking.data.server.memory;

import com.ewan.meworking.data.server.data.Data;
import lombok.Getter;

import java.util.HashMap;

public abstract class KnowledgePackage<I, D extends Data> {
    @Getter
    private final I identifier;
    public KnowledgePackage(I identifier) {
        this.identifier = identifier;
    }

    public KnowledgePackage() {
        identifier = null;
    } //For networking

    HashMap<Class<? extends D>, KnowledgeFragment<? extends D>> dataMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends KnowledgeFragment<D>> void register(T object){

        Class<? extends D> clazz = (Class<? extends D>) object.info.getClass();
        dataMap.put(clazz, object);

    }

    @SuppressWarnings("unchecked")
    public <T extends D> KnowledgeFragment<T> get(Class<T> clazz){
        return (KnowledgeFragment<T>) dataMap.get(clazz);
    }

}

