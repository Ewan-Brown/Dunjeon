package com.ewan.dunjeon.world.entities.ai;

import com.ewan.dunjeon.world.entities.Entity;

public abstract class GenericTask {

    protected Entity actor;

    protected GenericTask(Entity a, int p){
        actor = a;
    }

    public void update(){
//        System.out.println("\tUpdating Task : " + this.getClass());
    }

    Entity getActor(){
        return actor;
    }

}
