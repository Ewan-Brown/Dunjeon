package com.ewan.dunjeon.world.items;

import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.KinematicEntity;

import java.awt.*;

public abstract class Item {

    public Item(String name){
        this.name = name;
    }

    private String name;

    public String getName(){return name;}

    public void onWallCollision(BasicCell c){}

    public void onEntityCollision(KinematicEntity e){}

    public void onPickUp(KinematicEntity e){}

    public void onDropped(KinematicEntity e){}

    public abstract Shape getShape();

    public Point getGripPoint(){return new Point(0,0);}

    public abstract Point getMaxExtensionPoint();

}
