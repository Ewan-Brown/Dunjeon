package com.ewan.dunjeoncommon.data;

import com.ewan.dunjeon.server.world.CellPosition;
import com.ewan.dunjeoncommon.data.Datas.CellData;
import com.ewan.dunjeoncommon.memory.KnowledgePackage;

public class CellKnowledge extends KnowledgePackage<CellPosition, CellData> {


    public CellKnowledge(CellPosition identifier){
        super(identifier);
    }



}
