package com.ewan.dunjeon.world.entities;

import com.ewan.dunjeon.data.processing.DataProcessor;
import com.ewan.dunjeon.world.entities.memory.FloorKnowledge;
import com.ewan.dunjeon.world.entities.memory.Relationship;
import com.ewan.dunjeon.world.entities.memory.creaturedata.CreatureKnowledge;

import java.util.HashMap;

public class BasicMemoryBank extends DataProcessor {
    private HashMap<Long, CreatureKnowledge> creatureKnowledgeHashMap = new HashMap<>();
    private HashMap<Long, FloorKnowledge> floorKnowledgeHashMap = new HashMap<>();
    private HashMap<Long, Relationship> relationshipMap = new HashMap<>();
}
