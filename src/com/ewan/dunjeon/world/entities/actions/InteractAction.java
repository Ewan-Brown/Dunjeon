package com.ewan.dunjeon.world.entities.actions;

import com.ewan.dunjeon.world.World;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.furniture.Furniture;

public class InteractAction extends TimedAction{

    int x;
    int y;

    public InteractAction(int ticks, int x, int y) {
        super(ticks);
        this.x = x;
        this.y = y;
    }


    @Override
    public void onTimerComplete() {

        int cellLocX = actor.containingCell.getX() + x;
        int cellLocY = actor.containingCell.getY() + y;
        BasicCell entryCell = actor.containingCell.getLevel().getCellAt(cellLocX, cellLocY);

        if (entryCell != null && entryCell.canBeEntered(actor)) {
            World.getInstance().moveEntity(actor, entryCell);
        }

        Furniture f = entryCell.getFurniture();
        if(f != null){
            f.onInteract(actor);
        }
    }

    @Override
    public void cancel() {

    }
}
