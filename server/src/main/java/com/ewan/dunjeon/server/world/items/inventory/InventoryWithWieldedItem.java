package com.ewan.dunjeon.server.world.items.inventory;

import com.ewan.dunjeon.server.world.items.Item;

public class InventoryWithWieldedItem extends Inventory{

    private Item wieldedItem = null;

    public Item getWieldedItem() {
        return wieldedItem;
    }

    public void cycleWieldedItem(){
        if(!items.isEmpty()) {
            if (wieldedItem == null){
                wieldedItem = items.get(0);
            }
            else if(items.size() > 1){
                wieldedItem = items.get((items.indexOf(wieldedItem) + 1) % items.size());
            }
        }
    }

    @Override
    public void removeItem(Item item) {
        if(wieldedItem == item){
            wieldedItem = null;
        }
        super.removeItem(item);
    }
}
