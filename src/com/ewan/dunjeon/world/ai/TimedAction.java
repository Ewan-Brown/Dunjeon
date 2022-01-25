package com.ewan.dunjeon.world.ai;

import com.ewan.dunjeon.world.World;
import com.ewan.dunjeon.world.cells.BasicCell;

public abstract class TimedAction extends GenericAction{

    int ticks;

    public TimedAction(int ticks){
        super();
        this.ticks = ticks;
    }

    @Override
    public void update() {
        ticks--;
    }

    @Override
    public boolean isComplete() {
        return ticks < 0;
    }
}
