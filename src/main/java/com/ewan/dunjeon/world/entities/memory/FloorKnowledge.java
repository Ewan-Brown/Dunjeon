package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.world.entities.memory.celldata.CellKnowledge;
import com.ewan.dunjeon.world.entities.memory.creaturedata.CreatureKnowledge;
import com.ewan.dunjeon.world.entities.memory.furnituredata.FurnitureKnowledge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FloorKnowledge {
    private List<List<CellKnowledge>> cellKnowledgeList = new ArrayList<>();
    private List<FurnitureKnowledge> furnitureKnowledgeList = new ArrayList<>();
}
