package com.ewan.dunjeon.world.furniture;

import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.Updateable;
import com.ewan.dunjeon.world.cells.BasicCell;

import java.awt.*;

//TODO Make Furniture and Entity share a super class for obtaining containing cell and level.
public abstract class Furniture implements Updateable {

    public BasicCell containingCell;

    public Furniture() {

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
    public void update() {

    }
}
