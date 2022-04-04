package com.ewan.dunjeon.world.entities.actions;

import com.ewan.dunjeon.world.World;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.cells.Stair;
import sun.net.www.content.text.Generic;

public class UseStairsAction extends TimedAction {
    Stair s;

    public UseStairsAction(Stair entranceStairs, int ticks){
        super(ticks);
        this.s = entranceStairs;
    }


    @Override
    public void onTimerComplete() {
        BasicCell exitStairs = s.getConnection();
        if(exitStairs.canBeEntered(actor)) {
            World.getInstance().moveEntity(actor, exitStairs);
        }else{
            //TODO Maybe do something if the stairs are blocked here?
        }

    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void cancel() {

    }

}
