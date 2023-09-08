package com.ewan.dunjeon.server.world.entities.memory.celldata;

import com.ewan.dunjeon.data.Datas;
import com.ewan.dunjeon.server.world.WorldUtils;
import com.ewan.dunjeon.server.world.entities.memory.KnowledgePackage;

public class CellKnowledge extends KnowledgePackage<WorldUtils.CellPosition, Datas.CellData> {


    public CellKnowledge(WorldUtils.CellPosition identifier){
        super(identifier);
    }



}
