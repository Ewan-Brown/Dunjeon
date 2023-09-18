package com.ewan.meworking.data.server.memory.creaturedata;


import com.ewan.meworking.data.server.data.Datas;
import com.ewan.meworking.data.server.memory.KnowledgePackage;
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
