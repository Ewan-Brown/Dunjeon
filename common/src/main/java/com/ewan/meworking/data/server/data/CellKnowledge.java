package com.ewan.meworking.data.server.data;

import com.ewan.dunjeon.server.world.CellPosition;
import com.ewan.meworking.data.server.data.Datas.CellData;
import com.ewan.meworking.data.server.memory.KnowledgePackage;
import lombok.NoArgsConstructor;

public class CellKnowledge extends KnowledgePackage<CellPosition, CellData> {


    public CellKnowledge(CellPosition identifier){
        super(identifier);
    }
    public CellKnowledge(){}


}
