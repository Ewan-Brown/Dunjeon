package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.world.entities.creatures.senses.Sense;
import com.ewan.dunjeon.world.entities.memory.Brain;
import com.ewan.dunjeon.world.items.Item;

import java.util.List;

public class Player extends Creature {
    public Player(String name) {
        super(name);
        autoPickup = true;
    }

    @Override
    public Brain getBrain() {
        return null;
    }

    @Override
    protected void onPickupItem(Item i) {
        super.onPickupItem(i);

        System.out.println("Picked up item : " + i.getName());
    }

}
