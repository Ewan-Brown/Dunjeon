package com.ewan.dunjeon.world.entities;

import com.ewan.dunjeon.world.entities.ai.FSM.Explore;
import com.ewan.dunjeon.world.entities.ai.FSM.State;

import java.awt.*;
import java.util.function.Predicate;


public class Monster extends Entity{
    public Monster(Color c, Predicate<Entity> predicate) {
        super(c, 4, 20, 2, "Monster");
        isTargetPredicate = predicate;
        s = new Explore(this);
    }

    public Predicate<Entity> getTargetPredicate(){
        return isTargetPredicate;
    }

    Predicate<Entity> isTargetPredicate;
    State s;

    protected void setNewState(State s){
        this.s = s;
    }

    public void update() {
        super.update();
        if(getCurrentAction() == null) {
            s.update();
        }
        State newState = s.getNextState();
        if(newState != null){
            System.out.println("New State assigned! : " + newState.getClass());
            s = newState;
        }

    }

}
