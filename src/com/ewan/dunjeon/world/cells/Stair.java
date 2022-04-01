package com.ewan.dunjeon.world.cells;

import com.ewan.dunjeon.world.level.Floor;

import java.awt.*;

public class Stair extends BasicCell{
    private Stair(int x, int y, Floor f, Direction dir) {
        super(x, y, f);
        this.dir = dir;
    }

    public static Stair createDownwardsStair(int x, int y, Floor floor){
        return new Stair(x, y, floor, Direction.DOWN);
    }

    public static Stair createConnectingUpwardsStair(int x, int y, Floor f, Stair prevStair){
        Stair newStair = new Stair(x, y, f, Direction.DOWN);
        newStair.setConnection(prevStair);
        prevStair.setConnection(newStair);
        return newStair;
    }

    private void setConnection(Stair s){
        connection = s;
    }

    Stair connection;

    public enum Direction{
        UP, DOWN;
    }

    @Override
    public Color getColor() {
        return Color.WHITE;
    }

    Direction dir;
}
