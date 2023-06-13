package com.ewan.dunjeon.world.entities.memory.creaturedata;


import lombok.Getter;
import lombok.Setter;

/**
 * Represents basic information that is known about a Creature, from another Creature's perspective.
 */

public class CreatureKnowledge<T> {
    final long UUID;

    //TODO In the future make this compatible with 'partial' data that has some null fields and won't overwrite existing memory
    @Getter
    KineticStateData kineticStateData = null;
    @Getter
    VisualStateData visualStateData = null;

    public void updateVisualStateData(VisualStateData vState){ visualStateData = vState;}
    public void updateKineticStateData(KineticStateData kState){ kineticStateData = kState;}
    public CreatureKnowledge(long id){
        UUID = id;
    }
}
