package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.world.items.Item;

public class Player extends Creature {
    public Player(String name) {
        super(name);
        autoPickup = true;
    }

    @Override
    protected void onPickupItem(Item i) {
        super.onPickupItem(i);

        System.out.println("Picked up item : " + i.getName());
    }

}
