package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.world.data.Data;
import com.ewan.dunjeon.world.entities.memory.creaturedata.CreatureKnowledge;
import com.ewan.dunjeon.world.entities.memory.events.Event;
import com.ewan.dunjeon.world.entities.memory.events.EventStrategy;

import java.util.HashMap;

//Represents all the 'knowledge' an entity has
public class Brain {
    private HashMap<Long, CreatureKnowledge> creatureKnowledgeHashMap = new HashMap<>();
    private HashMap<Long, FloorKnowledge> floorKnowledgeHashMap = new HashMap<>();
    private HashMap<Long, Relationship> relationshipMap = new HashMap<>();
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
