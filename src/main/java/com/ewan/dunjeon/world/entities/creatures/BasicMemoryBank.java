package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.data.*;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.entities.memory.FloorKnowledge;
import com.ewan.dunjeon.world.entities.memory.KnowledgeFragment;
import com.ewan.dunjeon.world.entities.memory.celldata.CellKnowledge;
import com.ewan.dunjeon.world.entities.memory.creaturedata.CreatureKnowledge;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
public class BasicMemoryBank extends AbstractMemoryBank{

    private final HashMap<Long, CreatureKnowledge> creatureKnowledgeHashMap = new HashMap<>();
    private final HashMap<Long, FloorKnowledge> floorKnowledgeHashMap = new HashMap<>();
    private final HashMap<WorldUtils.CellPosition, CellKnowledge> cellKnowledgeHashMap = new HashMap<>();
    private final List<Event<?>> eventList = new ArrayList<>();

    //TODO Add structure so that we can have a list of indexed knowledges with identifiers, and 'non-indexed' ones that only decay and are never updated again. i.e decoy 'entities' that will never later get updated, and can cut down on some performance hits?

    //Unwrap data to figure out its context, and place it in the appropriate knowledge object
    //TODO Should turn this series of ifs into a list of strategies....
    @SuppressWarnings("unchecked")
    public void processWrappedData(DataWrapper<? extends Data, ?> wrappedData){
        if(wrappedData instanceof DataWrappers.EntityDataWrapper){
            List<Datas.EntityData> entityData = (List<Datas.EntityData>) wrappedData.getData();

            Long UUID = ((DataWrappers.EntityDataWrapper) wrappedData).getIdentifier();
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
                KnowledgeFragment kFragment = new KnowledgeFragment<>(entityDatum, wrappedData.getSourceSensor(), wrappedData.getTimestamp());
                creatureKnowledge.register(kFragment);
            }
        }


        //TODO Complete this
        if(wrappedData instanceof DataWrappers.CellDataWrapper){
            DataWrappers.CellDataWrapper wrappedCellData = (DataWrappers.CellDataWrapper) wrappedData;
            WorldUtils.CellPosition cellIdentifier = wrappedCellData.getIdentifier();

            CellKnowledge cellKnowledge;

            if(cellKnowledgeHashMap.containsKey(cellIdentifier)){
                cellKnowledge = cellKnowledgeHashMap.get(cellIdentifier);
            }else{
                cellKnowledge = new CellKnowledge(cellIdentifier);
                cellKnowledgeHashMap.put(cellIdentifier, cellKnowledge);
            }

            for (Datas.CellData cellDatum : wrappedCellData.getData()) {
                KnowledgeFragment kFragment = new KnowledgeFragment<>(cellDatum, wrappedData.getSourceSensor(), wrappedData.getTimestamp());
                cellKnowledge.register(kFragment);
            }
        }
    }

    public void processEventData(Event e){
        eventList.add(e);
    }

}
