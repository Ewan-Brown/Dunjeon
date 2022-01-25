package com.ewan.dunjeon.world.ai;

import com.ewan.dunjeon.world.entities.Entity;


//To be used in the time-step system.
public abstract class GenericAction {
    Entity actor;
    public abstract void update();
    public abstract void onStart();
    public abstract void onComplete();
    public abstract void onCancel();
    public abstract boolean isComplete();
    public void setActor(Entity e){
        actor = e;
    }
}
