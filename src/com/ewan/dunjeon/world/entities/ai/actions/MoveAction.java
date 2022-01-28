package com.ewan.dunjeon.world.entities.ai.actions;

import com.ewan.dunjeon.world.World;
import com.ewan.dunjeon.world.cells.BasicCell;

public class MoveAction extends TimedAction{

    int x;
    int y;
    boolean isDone = false;
    public MoveAction(int ticks, int x, int y) {
        super(ticks);
        this.x = x;
        this.y = y;
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void onTimerComplete() {
        int cellLocX = actor.containingCell.getX() + x;
        int cellLocY = actor.containingCell.getY() + y;
        BasicCell entryCell = actor.containingCell.getLevel().getCellAt(cellLocX, cellLocY);
        if(entryCell.canBeEntered(actor)){
            System.out.println("\t Success!");
            World.getInstance().moveEntity(actor, entryCell);
        }else{
            System.out.println("\t Failure!");
            throw new RuntimeException(cellLocX + " " + cellLocY);
        }
//        World.getInstance().movementProcessor.addMovement(actor, entryCell);
    }


    @Override
    public void cancel() {

    }
}
