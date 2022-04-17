package com.ewan.dunjeon.world.entities.ai;

import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.Entity;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class AttackTargetTask extends GenericTask{

    Entity target;
    List<BasicCell> currentPath = null;

    private AttackTargetTask(Entity a, int p, Entity t) {
        super(a, p);
        target = t;
    }

    @Override
    public void update() {
        super.update();


        //Check that target is still within vision
        if(!actor.getVisibleCells().contains(target.getContainingCell())){
            System.out.println("\t\tActor has left");
        }else{
            System.out.println("\t\tActor still in vision!");

            //Check if there is no path, or if its currentSize
            if(currentPath == null || !currentPath.get(currentPath.size()-1).equals(target.getContainingCell())){
                System.out.println("\t\tCreating a path");
                currentPath = WorldUtils.getAStarPath(getActor().getLevel(), getActor().getContainingCell(), target.getContainingCell(), getActor(), false );
            }
            System.out.println("\t\tCurrent Path : " + currentPath);
        }
    }
}
