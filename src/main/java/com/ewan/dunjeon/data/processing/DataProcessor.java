package com.ewan.dunjeon.data.processing;

import com.ewan.dunjeon.data.Data;
import com.ewan.dunjeon.world.entities.memory.FloorKnowledge;
import com.ewan.dunjeon.world.entities.memory.Relationship;
import com.ewan.dunjeon.world.entities.memory.creaturedata.CreatureKnowledge;
import com.ewan.dunjeon.data.Event;

import java.util.HashMap;
import java.util.List;


public class DataProcessor {

    private HashMap<Class<? extends Event>, EventStrategy<? extends Event>> eventStrategyMap = new HashMap<>();
    private HashMap<Class<? extends Data>, DataStrategy<? extends Data>> dataStrategyMap = new HashMap<>();

    public <E extends Event> void processEvent(E e){
        if(eventStrategyMap.containsKey(e.getClass())) {
            @SuppressWarnings("unchecked")
            EventStrategy<E> strategy = (EventStrategy<E>) eventStrategyMap.get(e.getClass());
            strategy.processEvent(e);
        }else{
            throw new RuntimeException("Brain attempted to process event : " + e.getClass() + " but no corresponding strategy found");
        }
    }
    public <D extends Data> void processData(D d){
        if(dataStrategyMap.containsKey(d.getClass())) {
            @SuppressWarnings("unchecked")
            DataStrategy<D> strategy = (DataStrategy<D>) dataStrategyMap.get(d.getClass());
            strategy.processData(d);
        }else{
            throw new RuntimeException("Brain attempted to process data : " + d.getClass() + " but no corresponding strategy found");
        }
    }
}
