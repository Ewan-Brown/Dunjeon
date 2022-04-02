package com.ewan.dunjeon.world.furniture;

import com.ewan.dunjeon.world.hasInventory;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.item.Item;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Container extends Furniture implements hasInventory {
    @Override
    public Color getColor() {
        return (getInventory().size() == 0) ? new Color(215, 215, 14): Color.yellow;
    }

    public void addToInventory(List<Item> items){ inventory.addAll(items);}

    @Override
    public void onInteract(Entity e) {
        //TODO Proper chest interactive UI
        e.getInventory().addAll(inventory);
        inventory.clear();
    }

    List<Item> inventory = new ArrayList<>();

    @Override
    public List<Item> getInventory() {
        return inventory;
    }
}
