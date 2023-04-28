package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.world.entities.memory.creaturedata.CreatureKnowledge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Brain {
    private HashMap<Long, CreatureKnowledge> creatureKnowledgeHashMap = new HashMap<>();
    private HashMap<Long, FloorKnowledge> floorKnowledgeHashMap = new HashMap<>();
    private List<Relationship> relationshipList = new ArrayList<>();

}
