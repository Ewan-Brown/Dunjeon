package com.ewan.dunjeon.world.furniture;

import com.ewan.dunjeon.world.entities.Entity;

import java.awt.*;

public class Stair extends Furniture{

    enum Direction{
        UP, DOWN
    }

    Direction direction;
    Stair connectingStair;

    public Stair(Direction d){

    }

    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public void onInteract(Entity e) {

    }
}
