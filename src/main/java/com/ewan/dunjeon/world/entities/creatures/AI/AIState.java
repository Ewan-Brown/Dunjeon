package com.ewan.dunjeon.world.entities.creatures.AI;

import com.ewan.dunjeon.world.entities.creatures.Creature;

/**
 * Represents the current most atomic level of the goal of the host of this object - could be "Move to target", "Explore unexplored area... etc"
 * These should be linked together with conditional branches using information from the AIStateData of the current AIProcess (And possibly past AIProcesses?)
 * <P></P>
 * This represents the a single current state of an entity, any information that should persist beyond this is to be stored in an extension of AIStateData
 */
public abstract class AIState {
    Creature hostEntity;
    public AIState(Creature e){
        hostEntity = e;
    }

    /**
     * Updates the AI, and performs actions for entity if required (for example: look around, search for target move towards target)
     */
    public abstract void process();

    public abstract <T extends AIStateData> T getStateData();

    /**
    Data Object, returns data representative of the current state of this AI process
     */
    public abstract static class AIStateData{
        public abstract boolean canContinue();
    }
}
