package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.world.entities.memory.CellMemory;
import com.ewan.dunjeon.world.entities.memory.FloorMemory;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Monster extends Creature{
    public Monster(Color c, String name) {
        super(c, name);
    }

    ArrayList<Point2D> currentPath = new ArrayList<>();

    @Override
    protected void processAI() {
//
//        if(currentPath.size() == 0) {
//
//            // Find a random accessible spot from memory:)
//            FloorMemory f = getFloorMemory(this.getFloor());
//
//            Point2D startNode = getContainingCell().getPoint2D();
//
//
//        }


    }


}
