package com.ewan.dunjeon.world.furniture;

import com.ewan.dunjeon.world.cells.BasicCell;

import java.awt.*;

public abstract class Furniture {

    public BasicCell containingCell;

    public Furniture() {

    }

    public abstract Color getColor();

    public boolean isBlocking(){
        return false;
    }
    public float getSize(){return 0.8f;}
    public float getPosX(){return containingCell.getX() + 0.5f;}
    public float getPosY(){return containingCell.getY() + 0.5f;}

    @Override
    public void update() {

    }
}
