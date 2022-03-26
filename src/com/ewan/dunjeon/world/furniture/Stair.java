package com.ewan.dunjeon.world.furniture;

import com.ewan.dunjeon.world.entities.Entity;

import java.awt.*;

public class Stair extends Furniture{

    public enum Direction{
        UP, DOWN
    }

    Direction direction;
    Stair connectingStair;

    public Stair(Direction d){
        this.direction = d;
    }

    @Override
    public Color getColor() {
        return Color.WHITE;
    }

    @Override
    public void onInteract(Entity e) {

    }
}
