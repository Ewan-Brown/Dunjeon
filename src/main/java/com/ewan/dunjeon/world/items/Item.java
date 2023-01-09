package com.ewan.dunjeon.world.items;

import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.Entity;

import java.awt.*;
import java.awt.geom.Area;
import java.util.function.Supplier;

public abstract class Item {

    public Item(String name){
        this.name = name;
    }

    private String name;

    public String getName(){return name;}

    public void onWallCollision(BasicCell c){}

    public void onEntityCollision(Entity e){}

    public void onPickUp(Entity e){}

    public void onDropped(Entity e){}

    public abstract Shape getShape();

}
