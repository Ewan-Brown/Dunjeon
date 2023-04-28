package com.ewan.dunjeon.world.entities.memory.creaturedata;


/**
 * Represents what Creature A knows about Creature B.
 * All info should be absolute (not relative to creature A) and
 * transferrable/shareable from Creature A to another, excepting Creature B
 */
public class CreatureKnowledge {

    final long UUID;

    private PhysicalStateData lastPhysicalState;
    private VisualStateData lastVisualState;
    private ActionStateData lastActionState;
    private HealthStateData lastHealthState;

    public CreatureKnowledge(long id){
        UUID = id;
    }

    public void updatePhysicalStateData(PhysicalStateData physicalState){
        lastPhysicalState = physicalState;
    }
    public void updateVisualState(VisualStateData visualStateData){ lastVisualState = visualStateData;}
    public void updateActionState(ActionStateData actionStateData){lastActionState = actionStateData;}
    public void updateHealthStateData(HealthStateData healthStateData){lastHealthState = healthStateData;}
}
