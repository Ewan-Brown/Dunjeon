package com.ewan.dunjeon.world.entities.ai.actions;

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
