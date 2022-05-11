package com.ewan.dunjeon.world.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Inventory {

    private List<Item> items;

    public static void moveItem(Inventory source, Inventory destination, Item i){
        source.removeItem(i);
        destination.addItem(i);
    }

    public void addItem(Item i){
        if (items.contains(i)) {
            throw new Error("Attempted to add an item to an inventory, but it's already in there!");
        }
        items.add(i);
    }

    public void removeItem(Item i){
        if (!items.remove(i)) {
            throw new Error("Attempted to remove an item from an inventory that did not contain the item.");
        }

    }

    public List<Item> getAllItems() {
        return items;
    }

    /*
    https://stackoverflow.com/questions/40989500/java-filter-list-to-generic-type-t
     */
    public <T extends Item> List<T> getItemsOfType(Class<T> clazz){
        return items.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toList());
    }
}
