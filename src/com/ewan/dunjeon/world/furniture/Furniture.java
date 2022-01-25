package com.ewan.dunjeon.world.furniture;

import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.ItemHolder;
import com.ewan.dunjeon.world.Updateable;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.item.Item;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

//TODO Make Furniture and Entity share a super class for obtaining containing cell and level.
public abstract class Furniture implements ItemHolder, Updateable {

    public BasicCell containingCell;

    public Furniture(){

    }

    /**
     * Could be null if the furniture doesn't have a color right now (i.e is invisible?)
     * @return
     */
    public abstract Color getColor();

    public boolean isBlocking(){
        return false;
    }

    public abstract void onInteract(Entity e);

    @Override
    public List<Item> getItems() {
        return new ArrayList<>();
    }

    @Override
    public void update() {

    }
}
