package com.ewan.dunjeon.world.entities.ai;

import com.ewan.dunjeon.world.entities.Entity;

public abstract class GenericTask {

    protected Entity actor;
    private final int priority_UNUSED;
    protected boolean isCompleted = false;

    public GenericTask(Entity a, int p){
        actor = a;
        priority_UNUSED = p;
    }

    public int getPriority(){
        return priority_UNUSED;
    }

    public void update(){
//        System.out.println("\tUpdating Task : " + this.getClass());
    }

    public boolean isCompleted(){ return isCompleted; }

    public void complete(){
        isCompleted = true;
    }

    Entity getActor(){
        return actor;
    }

}
