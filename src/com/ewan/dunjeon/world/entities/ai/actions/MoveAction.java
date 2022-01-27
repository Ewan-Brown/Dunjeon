package com.ewan.dunjeon.world.entities.ai.actions;

import com.ewan.dunjeon.world.World;
import com.ewan.dunjeon.world.cells.BasicCell;

public class MoveAction extends TimedAction{

    int x;
    int y;
    boolean isDone = false;
    public MoveAction(int ticks, int x, int y) {
        super(ticks);
        System.out.printf("New Move Action : (%d, %d)\n",x,y);
        this.x = x;
        this.y = y;
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void onTimerComplete() {
        System.out.println("MoveAction Complete!");
        int cellLocX = actor.containingCell.getX() + x;
        int cellLocY = actor.containingCell.getY() + y;
        BasicCell entryCell = actor.containingCell.getLevel().getCellAt(cellLocX, cellLocY);

        if (entryCell != null && entryCell.canBeEntered(actor)) {
            System.out.println("Sending move object!");
            World.getInstance().movementProcessor.addMovement(actor, entryCell);
        }else{
            System.out.println("Failed to enter cell");
            actor.setNewAction(null);
            this.cancel();
        }
    }


    @Override
    public void cancel() {

    }
}
