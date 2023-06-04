package com.ewan.dunjeon.world.entities.memory.creaturedata;


import com.ewan.dunjeon.world.entities.memory.StateData;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents basic information that is known about a Creature, from another Creature's perspective.
 */
public class CreatureKnowledge<T> {
    final long UUID;

    @Getter
    @Setter
    PhysicalStateData physicalStateData = null;

    @Getter
    @Setter
    VisualStateData visualStateData = null;
    public CreatureKnowledge(long id){
        UUID = id;
    }
}
