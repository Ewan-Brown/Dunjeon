package com.ewan.dunjeon.world.entities;

import com.ewan.dunjeon.data.Data;
import com.ewan.dunjeon.data.DataWrapper;
import com.ewan.dunjeon.data.DataWrappers;
import com.ewan.dunjeon.data.Datas;
import com.ewan.dunjeon.data.processing.EventProcessor;
import com.ewan.dunjeon.world.entities.memory.FloorKnowledge;
import com.ewan.dunjeon.world.entities.memory.Relationship;
import com.ewan.dunjeon.world.entities.memory.creaturedata.CreatureKnowledge;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class BasicMemoryBank extends EventProcessor {

    private final HashMap<Long, CreatureKnowledge> creatureKnowledgeHashMap = new HashMap<>();
    private final HashMap<Long, FloorKnowledge> floorKnowledgeHashMap = new HashMap<>();
    private final HashMap<Long, Relationship> relationshipMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public void processWrappedData(DataWrapper<? extends Data, ?> wrappedData){
        if(wrappedData instanceof DataWrappers.EntityDataWrapper){
            List<Datas.EntityData> entityData = (List<Datas.EntityData>) wrappedData.getData();

            long UUID = ((DataWrappers.EntityDataWrapper) wrappedData).getIdentifier();
            CreatureKnowledge creatureKnowledge;
            if(!creatureKnowledgeHashMap.containsKey(UUID)){
                creatureKnowledge = creatureKnowledgeHashMap.get(UUID);
            }else{
                creatureKnowledge = new CreatureKnowledge(UUID);
                creatureKnowledgeHashMap.put(UUID, creatureKnowledge);
            }

            for (Datas.EntityData entityDatum : entityData) {
                creatureKnowledge.register(entityDatum);

            }
        }
    }
}
