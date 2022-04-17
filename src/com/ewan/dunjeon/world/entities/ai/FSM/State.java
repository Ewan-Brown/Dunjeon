package com.ewan.dunjeon.world.entities.ai.FSM;

import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.entities.Monster;

public abstract class State {

    /**
     * Get the entity's next state, if there is a one. If the current state wishes to continue, this returns null.
     * @return
     */
    public abstract State getNextState();
    public Monster actor;

    public State(Monster a){
        actor = a;
    }

    public abstract void update();

}
