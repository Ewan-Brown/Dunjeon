package com.ewan.dunjeon.world.entities.ai.actions;

public abstract class TimedAction extends GenericAction{

    int ticks;

    public TimedAction(int ticks){
        super();
        this.ticks = ticks;
    }

    public abstract void onTimerComplete();

    @Override
    public void update() {
        ticks--;
        if(isDone()){
            onTimerComplete();
        }
    }

    @Override
    public boolean isDone() {
        return ticks <= 0;
    }
}
