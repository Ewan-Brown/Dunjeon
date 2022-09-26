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

    public float getOffsetX(){return 0; }
    public float getOffsetY(){return 0; }
    public float getX(){return containingCell.getX() + 0.5f + getOffsetX();}
    public float getY(){return containingCell.getY() + 0.5f + getOffsetY();}

    @Override
    public void update() {

    }
}
