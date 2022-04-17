package com.ewan.dunjeon.world.entities.actions;

import com.ewan.dunjeon.world.entities.Entity;


//To be used in the time-step system.
public abstract class GenericAction {
    Entity actor;
    public abstract void update();
    public void cancel(){}
    public abstract boolean isDone();
    public void setActor(Entity e){
        actor = e;
    }
}
