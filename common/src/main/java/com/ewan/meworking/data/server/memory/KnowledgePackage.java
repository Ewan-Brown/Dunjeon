package com.ewan.meworking.data.server.memory;

import com.ewan.meworking.data.server.data.Data;
import lombok.Getter;

import java.util.HashMap;

//TODO look at removing concrete implementations of this class - it makes networking hard ;( and each implementation is pretty empty.
public class KnowledgePackage<I, D extends Data> {
    @Getter
    private final I identifier;
    public KnowledgePackage(I identifier) {
        this(identifier, new HashMap<>());
    }

    //Used for networking. Needs to be implented in each subclass as constructors are not inherited
    public KnowledgePackage(I identifier, HashMap<Class<? extends D>, KnowledgeFragment<? extends D>> dataMap){
        this.identifier = identifier;
        this.dataMap = dataMap;
    }

    final HashMap<Class<? extends D>, KnowledgeFragment<? extends D>> dataMap;

    @SuppressWarnings("unchecked")
    public <T extends KnowledgeFragment<D>> void register(T object){

        Class<? extends D> clazz = (Class<? extends D>) object.info.getClass();
        dataMap.put(clazz, object);

    }

    /**
     * At the moment this should only be used by networking code!
     * @return
     */
    public HashMap<Class<? extends D>, KnowledgeFragment<? extends D>> getDataMap() {
        return dataMap;
    }


    @SuppressWarnings("unchecked")
    public <T extends D> KnowledgeFragment<T> get(Class<T> clazz){
        return (KnowledgeFragment<T>) dataMap.get(clazz);
    }

}

