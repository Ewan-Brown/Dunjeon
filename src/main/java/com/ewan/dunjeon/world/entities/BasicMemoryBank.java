package com.ewan.dunjeon.world.entities;

import com.ewan.dunjeon.data.*;
import com.ewan.dunjeon.world.entities.memory.FloorKnowledge;
import com.ewan.dunjeon.world.entities.memory.creaturedata.CreatureKnowledge;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
public class BasicMemoryBank {

    private final HashMap<Long, CreatureKnowledge> creatureKnowledgeHashMap = new HashMap<>();
    private final HashMap<Long, FloorKnowledge> floorKnowledgeHashMap = new HashMap<>();
    private final List<Event<?>> eventList = new ArrayList<>();


    //Unwrap data to figure out its context, and place it in the appropriate knowledge object
    //TODO Should turn this series of ifs into a list of strategies....
    @SuppressWarnings("unchecked")
    public void processWrappedData(DataWrapper<? extends Data, ?> wrappedData){
        if(wrappedData instanceof DataWrappers.EntityDataWrapper){
            List<Datas.EntityData> entityData = (List<Datas.EntityData>) wrappedData.getData();

            long UUID = ((DataWrappers.EntityDataWrapper) wrappedData).getIdentifier();
            CreatureKnowledge creatureKnowledge;

            //Create knowledge object for this creature if it doesn't already exist
            if(creatureKnowledgeHashMap.containsKey(UUID)){
                creatureKnowledge = creatureKnowledgeHashMap.get(UUID);
            }else{
                creatureKnowledge = new CreatureKnowledge(UUID);
                creatureKnowledgeHashMap.put(UUID, creatureKnowledge);
            }

            //Register the data for this creature.
            for (Datas.EntityData entityDatum : entityData) {
                creatureKnowledge.register(entityDatum);
            }
        }
    }

    public void processEventData(Event e){
        eventList.add(e);
    }
}
