package com.ewan.dunjeon.world.furniture;

import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.Updateable;
import com.ewan.dunjeon.world.cells.BasicCell;

import java.awt.*;

public abstract class Furniture implements Updateable {

    public BasicCell containingCell;

    public Furniture() {

    }

    public abstract Color getColor();

    public boolean isBlocking(){
        return false;
    }
    public float getSize(){return 0.8f;}
    public float getCenterX(){return containingCell.getX() + 0.5f;}
    public float getCenterY(){return containingCell.getY() + 0.5f;}

    @Override
    public void update() {

    }
}