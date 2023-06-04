package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.world.entities.memory.celldata.CellKnowledge;
import com.ewan.dunjeon.world.entities.memory.creaturedata.CreatureKnowledge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FloorKnowledge {
    private List<List<CellKnowledge>> cellKnowledgeList = new ArrayList<>();
    private List<Long> lastKnownCreaturesUUIDS = new ArrayList<>();
}
