package com.ewan.dunjeon.server.world.entities.memory.creaturedata;


import com.ewan.dunjeon.data.Datas;
import com.ewan.dunjeon.server.world.entities.memory.KnowledgePackage;
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
