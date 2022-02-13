package com.ewan.dunjeon.world.entities;

import com.ewan.dunjeon.world.entities.ai.GenericTask;
import com.ewan.dunjeon.world.entities.ai.SearchGivenArea;

import java.awt.*;


public class Monster extends Entity{
    public Monster(Color c) {
        super(c, 3, 5);
    }

    GenericTask task;

    @Override
    public void update() {
        super.update();
        if(task == null){
            System.out.println("New Task Created");
            task = SearchGivenArea.RandomSearchRememberedArea(this);
        }
        task.update();
//        if (getCurrentAction() == null) {
//            tryToWalk();
//        }

    }



    public void tryToWalk(){

    }
}
