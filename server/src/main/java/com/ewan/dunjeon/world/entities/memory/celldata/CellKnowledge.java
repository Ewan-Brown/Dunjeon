package com.ewan.dunjeon.world.entities.memory.celldata;

import com.ewan.dunjeon.data.Datas;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.entities.memory.KnowledgePackage;

import java.util.HashMap;

public class CellKnowledge extends KnowledgePackage<WorldUtils.CellPosition, Datas.CellData> {


    public CellKnowledge(WorldUtils.CellPosition identifier){
        super(identifier);
    }



}
