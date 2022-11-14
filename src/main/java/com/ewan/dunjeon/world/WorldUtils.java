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

    public static final float ENTITY_WITHIN_TILE_THRESHOLD = 0.3f;

    //TODO Reduce these after by making a 'HasPosition' interface?
    public static float getRawDistance(Entity e1, Entity e2){
        return getRawDistance(e1.getPosX(), e2.getPosX(), e1.getPosY(), e2.getPosY());
    }
    public static float getRawDistance(BasicCell c1, BasicCell c2){
        return getRawDistance(c1.getX(), c2.getX(), c1.getY(), c2.getY());
    }

    public static float getRawDistance(BasicCell c, Entity e){
        return getRawDistance(c.getX(), e.getPosX(), c.getY(), e.getPosY());
    }
    public static float getRawDistance(Furniture f, Entity e){
        return getRawDistance(f.getPosX(), e.getPosX(), f.getPosY(), e.getPosY());
    }
    public static float getRawDistance(Interactable f, Entity e){
        return getRawDistance(f.getPosX(), e.getPosX(), f.getPosY(), e.getPosY());
    }

    public static float getRawDistance(float x1, float x2, float y1, float y2){
        return (float)Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
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
