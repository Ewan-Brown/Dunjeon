package com.ewan.dunjeon.world;

import com.ewan.dunjeon.game.Main;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.furniture.Furniture;
import com.ewan.dunjeon.world.level.Floor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.abs;


public class WorldUtils {

    //TODO Reduce these after by making a 'HasPosition' interface?
    public static float getRawDistance(Entity e1, Entity e2){
        return (float)Math.sqrt(Math.pow(e1.getPosX() - e2.getPosX(), 2) + Math.pow(e1.getPosY() - e2.getPosY(), 2));
    }
    public static float getRawDistance(BasicCell c1, BasicCell c2){
        return (float)Math.sqrt(Math.pow(c1.getX() - c2.getX(), 2) + Math.pow(c1.getY() - c2.getY(), 2));
    }

    public static float getRawDistance(BasicCell c, Entity e){
        return (float)Math.sqrt(Math.pow(c.getX() - e.getPosX(), 2) + Math.pow(c.getY() - e.getPosY(), 2));
    }
    public static float getRawDistance(Furniture f, Entity e){
        return (float)Math.sqrt(Math.pow(f.getPosX() - e.getPosY(), 2) + Math.pow(f.getPosY() - e.getPosY(), 2));
    }
    public static float getRawDistance(Interactable f, Entity e){
        return (float)Math.sqrt(Math.pow(f.getPosX() - e.getPosX(), 2) + Math.pow(f.getPosY() - e.getPosY(), 2));
    }

    public static boolean isAdjacent(BasicCell b1, BasicCell b2){
        if(b1 == b2){
            throw new IllegalArgumentException();
        }

        int xDiff = b2.getX() - b1.getX();
        int yDiff = b2.getY() - b1.getY();

        return Math.abs(yDiff) < 2 && Math.abs(xDiff) < 2;
    }
}
