package com.ewan.dunjeoncommon.memory.creaturedata;


import com.ewan.dunjeoncommon.data.Datas;
import com.ewan.dunjeoncommon.memory.KnowledgePackage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Represents basic information that is known about a Creature, from another Creature's perspective.
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreatureKnowledge extends KnowledgePackage<Long, Datas.EntityData> {

    public CreatureKnowledge(Long id){
        super(id);
    }

}
