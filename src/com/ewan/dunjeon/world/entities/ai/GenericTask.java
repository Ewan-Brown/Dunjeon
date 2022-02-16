package com.ewan.dunjeon.world.entities.ai;

import com.ewan.dunjeon.world.entities.Entity;

public class GenericTask {
    protected Entity actor;

    public GenericTask(Entity a){
        actor = a;
    }

    public void update(){

    }

    Entity getActor(){
        return actor;
    }

}
