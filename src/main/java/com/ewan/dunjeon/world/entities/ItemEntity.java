package com.ewan.dunjeon.world.entities;

import com.ewan.dunjeon.world.items.Item;

public class ItemEntity extends Entity{

    Item i;

    public ItemEntity(Item i) {
        super(i.getRenderData().c, i.getName());
    }
}
