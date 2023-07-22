package com.ewan.dunjeon.world.entities.AI;


import com.ewan.dunjeon.world.entities.creatures.Creature;
import lombok.Getter;

public abstract class CreatureController<C extends Creature> {

    public CreatureController(C connectedCreature) {
        this.connectedCreature = connectedCreature;
    }

    public abstract void update();

    @Getter
    private C connectedCreature;


}
