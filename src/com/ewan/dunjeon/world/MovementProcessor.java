package com.ewan.dunjeon.world;

import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.Entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MovementProcessor {

    private List<Movement> movements = new ArrayList<>();

    private static class Movement{
        private Movement(Entity e, BasicCell c){
            actor = e;
            target = c;
        }

        Entity actor;
        BasicCell target;
    }

    public void addMovement(Entity e, BasicCell c){
        movements.add(new Movement(e, c));
    }

    private int calculatePriority(Movement m){
        return 1;
    }

    public void processMovements(){
        for (Movement movement : movements) {
            if(movement.target.canBeEntered(movement.actor)){
                World.getInstance().moveEntity(movement.actor, movement.target);
            }
        }
    }
}
