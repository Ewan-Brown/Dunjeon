package com.ewan.dunjeon.world.entities.memory.creaturedata;

import java.util.HashMap;
import java.util.Map;

public class ClassMap<T> {

    private Map<Class<? extends T>, T> map = new HashMap<>();

    public T get(Class<? extends T> clazz) {
        return map.get(clazz);
    }

    public void put(Class<? extends T> clazz, T instance) {
        if (map.containsKey(clazz)) {
            map.replace(clazz, instance);
        } else {
            map.put(clazz, instance);
        }
    }
}

