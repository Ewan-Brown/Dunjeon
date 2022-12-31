package com.ewan.dunjeon.world.entities;

import com.ewan.dunjeon.world.items.Item;

public class MovingItem extends Entity{

    Item i;

    public MovingItem(Item i) {
        super(i.getRenderData().c, i.getName());
    }
}
