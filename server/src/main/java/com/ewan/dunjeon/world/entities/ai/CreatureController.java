package com.ewan.dunjeon.world.entities.ai;


import com.ewan.dunjeon.world.entities.creatures.Creature;
import lombok.Getter;

public abstract class CreatureController<C extends Creature> {

    public CreatureController(C connectedCreature) {
        this.connectedCreature = connectedCreature;
    }

    public abstract void update();

    @Getter
    private final C connectedCreature;


}
