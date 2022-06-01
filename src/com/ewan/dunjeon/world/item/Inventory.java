package com.ewan.dunjeon.world.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Inventory {

    private Set<Item> items;

    public static void moveItem(Inventory source, Inventory destination, Item i){
        source.removeItem(i);
        destination.addItem(i);
    }

    public void addItem(Item i){
        items.add(i);
    }

    public void removeItem(Item i){
        if (!items.remove(i)) {
            throw new RuntimeException("Attempted to remove an item from an inventory that did not contain the item.");
        }

    }

    public Set<Item> getAllItems() {
        return items;
    }

}
