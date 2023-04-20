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
    public double getSize(){return 0.8f;}
    public double getPositionX(){return containingCell.getX() + 0.5f;}
    public double getPositionY(){return containingCell.getY() + 0.5f;}

    public void update() {

    }
}
