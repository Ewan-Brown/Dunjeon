package com.ewan.dunjeon.world.furniture;

import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.item.Inventory;

import java.awt.*;

public class Container extends Furniture{
    @Override
    public Color getColor() {
        return (getInventory() ==  null || getInventory().getAllItems() == null || getInventory().getAllItems().isEmpty()) ? new Color(215, 215, 14): Color.yellow;
    }

    private Inventory inventory = new Inventory();

    @Override
    public void onInteract(Entity e) {
        //TODO Proper chest interactive UI
//        e.getInventory().addAll(inventory);
//        inventory.clear();
    }

    public Inventory getInventory(){return inventory;}
}
