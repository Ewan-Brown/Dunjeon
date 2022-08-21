package com.ewan.dunjeon.world.furniture;

import com.ewan.dunjeon.world.entities.Entity;

import java.awt.*;

public class Container extends Furniture{

    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public void onInteract(Entity e) {
        //TODO Proper chest interactive UI
//        e.getInventory().addAll(inventory);
//        inventory.clear();
    }

}
