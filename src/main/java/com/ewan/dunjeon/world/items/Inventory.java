package com.ewan.dunjeon.world.items;

import java.util.List;
import java.util.function.Predicate;

public class Inventory {

    private List<Item> items;

    public void addItem(Item item){
        items.add(item);
    }

    public void removeItem(Item item){
        items.remove(item);
    }

    public Item getItem(Predicate<Item> predicate){
        return items.stream().filter(predicate).findFirst().orElseThrow();
    }


}
