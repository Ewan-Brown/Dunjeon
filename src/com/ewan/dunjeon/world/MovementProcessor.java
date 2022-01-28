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

        @Override
        public String toString() {
            return String.format("[Movement] (%d, %d) to (%d, %d)", actor.getX(), actor.getY(), target.getX(), target.getY());
        }
    }

    public void addMovement(Entity e, BasicCell c){
        movements.add(new Movement(e, c));
    }

    private int calculatePriority(Movement m){
        return 1;
    }

    public void processMovements(){
        if(movements.size() == 0){
            System.out.println("No Movements");
        }else{
            System.out.printf("Processing %d Movements\n", movements.size());
        }
        for (Movement movement : movements) {
            System.out.println(movement);

        }
        movements.clear();
    }
}
