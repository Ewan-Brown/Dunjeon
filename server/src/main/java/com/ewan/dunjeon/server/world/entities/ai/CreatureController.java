package com.ewan.dunjeon.server.world.entities.ai;


import com.ewan.dunjeon.server.world.entities.creatures.Creature;
import com.ewan.dunjeon.server.world.entities.creatures.CreatureControls;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import lombok.Getter;

public abstract class CreatureController<C extends Creature, D extends CreatureControls<C>> {

    @SuppressWarnings("unchecked")
    public CreatureController(C connectedCreature) {
        this.connectedCreature = connectedCreature;
        controls = (D) connectedCreature.getControls(); //TODO Add safeguard (linter?) to ensure that Creature.getControls() returns controls for the parent class's type
    }

    public BasicMemoryBank getBasicMemoryBank(){
        return connectedCreature.getMemoryBank();
    }

    private C connectedCreature;

    public abstract void update(double stepSize);
    @Getter
    protected final D controls;

}
