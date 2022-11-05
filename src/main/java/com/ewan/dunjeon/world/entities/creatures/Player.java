package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.world.entities.memory.CellMemory;
import com.ewan.dunjeon.world.sounds.RelativeSoundEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Predicate;

public class Player extends Creature{
    public Player(Color c, String name) {
        super(c, name);
    }

    @Override
    protected void processAI() {
        //Thinky thinky
    }

    @Override
    public void processSound(RelativeSoundEvent event) {
        super.processSound(event);
        int sourceX = (int)event.abs().sourceLocation().getX();
        int sourceY = (int)event.abs().sourceLocation().getY();
        boolean isVisible = !getFloorMemory(event.abs().sourceFloor()).getDataAt(sourceX, sourceY).isOldData();
        String message = isVisible ? event.abs().soundMessageIfVisible() : event.abs().soundMessageIfNotVisible();
        if(!message.isEmpty()) {
            System.out.println("["+message+"]");
        }
    }

    /**
     * Works as expected now :)
     */
    public ArrayList<Point> getListOfAccessibleNodesFromMemoryWithCriteria(){
        Point startNode = new Point((int)Math.floor(getPosX()), (int)Math.floor(getPosY()));
        ArrayList<Point> toExplore = new ArrayList<>();
        ArrayList<Point> accessibleNodes = new ArrayList<>();
        toExplore.add(startNode);

        while(!toExplore.isEmpty()){
            Point currentNode = toExplore.get(0);
            accessibleNodes.add(currentNode);
            toExplore.remove(currentNode);
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (i == 0 && j == 0) continue;
                    Point neighbor = new Point((int)currentNode.getX() + i, (int)currentNode.getY() + j);
                    CellMemory cellMemory = getFloorMemory(getFloor()).getDataAt((int)neighbor.getX(), (int)neighbor.getY());
                    if(cellMemory != null && cellMemory.enterable == CellMemory.EnterableStatus.OPEN && !accessibleNodes.contains(neighbor)){
                        toExplore.add(neighbor);
                    }
                }
            }
        }
        return accessibleNodes;
    }
}
