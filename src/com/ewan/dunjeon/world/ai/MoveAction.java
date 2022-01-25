package com.ewan.dunjeon.world.ai;

import com.ewan.dunjeon.world.World;
import com.ewan.dunjeon.world.cells.BasicCell;

import java.awt.*;

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
        int cellLocX = actor.containingCell.getX() + x;
        int cellLocY = actor.containingCell.getY() + y;
        BasicCell entryCell = actor.containingCell.getLevel().getCellAt(cellLocX, cellLocY);

        if (entryCell != null && entryCell.canBeEntered(actor)) {
            World.getInstance().movementProcessor.addMovement(actor, entryCell);
        }
    }

    @Override
    public void onCancel() {

    }
}
