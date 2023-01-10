package com.ewan.dunjeon.world.items.inventory;

import com.ewan.dunjeon.world.items.Item;

import java.util.*;
import java.util.function.Predicate;

public class Inventory {

    protected List<Item> items = new ArrayList<>();

    public void addItem(Item item){
        if(items.contains(item)){
            throw new RuntimeException("Attempted to add duplicate item to inventory list : " + item.getName());
        }
        items.add(item);
    }

    public void removeItem(Item item){
        items.remove(item);
    }

    public Item getItem(Predicate<Item> predicate){
        return items.stream().filter(predicate).findFirst().orElseThrow();
    }



}
