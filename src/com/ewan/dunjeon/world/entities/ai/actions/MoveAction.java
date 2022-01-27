package com.ewan.dunjeon.world.entities.ai.actions;

import com.ewan.dunjeon.world.World;
import com.ewan.dunjeon.world.cells.BasicCell;

public class MoveAction extends TimedAction{

    int x;
    int y;
    public MoveAction(int ticks, int x, int y) {
        super(ticks);
        this.x = x;
        this.y = y;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onComplete() {
        System.out.println("MoveAction Complete!");
        int cellLocX = actor.containingCell.getX() + x;
        int cellLocY = actor.containingCell.getY() + y;
        BasicCell entryCell = actor.containingCell.getLevel().getCellAt(cellLocX, cellLocY);

        if (entryCell != null && entryCell.canBeEntered(actor)) {
            System.out.println("Attempting movement!");
            World.getInstance().movementProcessor.addMovement(actor, entryCell);
        }else{
            System.out.println("Failed to enter cell");
        }
    }

    @Override
    public void onCancel() {

    }
}
