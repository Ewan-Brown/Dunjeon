package com.ewan.dunjeon.world.entities;

import com.ewan.dunjeon.world.items.Item;

public class ItemAsEntity extends Entity{

    Item i;


    public ItemAsEntity(Item i) {
        super(i.getRenderData().c, i.getName());
    }
}
