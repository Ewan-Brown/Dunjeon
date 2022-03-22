package com.ewan.dunjeon.world.entities;

import com.ewan.dunjeon.graphics.LiveDisplay;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.ai.AttackTargetTask;
import com.ewan.dunjeon.world.entities.ai.GenericTask;
import com.ewan.dunjeon.world.entities.ai.SearchGivenArea;

import java.awt.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class Monster extends Entity{
    public Monster(Color c, Predicate<Entity> predicate) {
        super(c, 4, 5);
        isTargetPredicate = predicate;
    }

    GenericTask task;
    Predicate<Entity> isTargetPredicate;

    /**
     * There's alot going on here. <p>
     * There are 2 levels to Monster behavior. The 'Task' and the 'Action'.
     * The 'action' is the lower level, representing what the entity is physically doing
     * </p>
     * <p>
     * The 'Task' is the higher level, and only used for AI. It represents the 'desire' of the entity, what it is trying to accomplish.
     * Every update, the game checks if the entity is 'idle' (i.e its current action is null). If so, the game will first check whether to continue the current task, and then the (possibly new) task will decide what the next action is.
     * </p>
     */

    public void update() {
//        System.out.println("[Update]\n");
        super.update();

//        System.out.println("\tAction :" + getCurrentAction());
        if(getCurrentAction() == null){
            /*
                //TODO This is what should happen here:
                 - Loop through all possible tasks
                 - Find the one with highest priority
                 - Either continue doing that if it's already going or start it fresh
             */
            task = SearchGivenArea.RandomSearchRememberedArea(this, 2);

//            Entity target = tryToFindTarget();
//            if(target == null && task == null){
//            }else if(target != null){
//                task = new AttackTargetTask(this, 1, target);
//            }

        }
        task.update();
        if(task.isCompleted()) {
            System.out.println("\tTask Complete");
            task = null;
        }
    }

    /**
     * UNFINISHED, NEEDS LOTSA WORK
     * @return
     */
    public Entity tryToFindTarget(){
        if(task instanceof AttackTargetTask){

        }
        else {
            System.out.println("\tTrying to find target");
            Set<BasicCell> visibleCells = getVisibleCells();
            LiveDisplay.setDebugCells(new ArrayList<>(visibleCells));
            System.out.println("\t\t Visible cells : " + visibleCells.size());
            BasicCell target = visibleCells.stream().filter(
                    basicCell -> isTargetPredicate.test(basicCell.getEntity())).findAny().orElse(null);

            if (target != null) {
                System.out.println("\t\tTarget Cell : " + target.toString());
                Entity targetEntity = target.getEntity();
                System.out.println("\t\tTarget : " + targetEntity);
//            task = new AttackTargetTask(this, 0, targetEntity);
                return targetEntity;
            } else {
                System.out.println("\t\tTarget null");
                return null;
            }
        }
        return null;
    }


}
