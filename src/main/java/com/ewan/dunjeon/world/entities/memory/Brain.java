package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.world.data.Data;
import com.ewan.dunjeon.world.entities.memory.creaturedata.CreatureKnowledge;
import com.ewan.dunjeon.world.entities.memory.events.Event;
import com.ewan.dunjeon.world.entities.memory.events.EventStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//Represents all the 'knowledge' an entity has
public class Brain {
    private HashMap<Long, CreatureKnowledge> creatureKnowledgeHashMap = new HashMap<>();
    private HashMap<Long, FloorKnowledge> floorKnowledgeHashMap = new HashMap<>();
    private HashMap<Long, Relationship> relationshipMap = new HashMap<>();
    private HashMap<? extends Event, EventStrategy> eventStrategyMap = new HashMap<>();

    public void processEvent(Event e){
        if(eventStrategyMap.containsKey(e.getClass())) {
            EventStrategy strategy = eventStrategyMap.get(e.getClass());
            if (strategy != null) {
                strategy.processEvent(e);
            }
        }
    }

    public void processData(Data d){

    }
}
