package com.ewan.dunjeon.world.entities.ai.FSM;

import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.AttackData;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.entities.Monster;
import com.ewan.dunjeon.world.entities.actions.MeleeAttackAction;
import com.ewan.dunjeon.world.entities.actions.MoveAction;

import java.util.List;
import java.util.function.Predicate;

public class Attack extends State {
    Entity target;
    List<BasicCell> currentPathToTarget;
    BasicCell lastLocationOfTarget = null;
    boolean isTargetReachable;

    public Attack(Monster actor, Entity target) {
        super(actor);
        this.target = target;
        isTargetReachable = true;
        lastLocationOfTarget = target.getContainingCell();
    }

    @Override
    public State getNextState() {
        //If the current path is still null once the code reaches here, then there must be no valid path to the target, or it has dissapeared.
        if (!isTargetReachable) {
            if(lastLocationOfTarget != null){
                System.out.println("Target gone, exploring their last location");
                return new Explore(actor, lastLocationOfTarget);
            }else {
                System.err.println("Target dissapeared, attempted to track but last location logged is null! Should not happen.");
                throw new NullPointerException();
            }
        }
        return null;
    }

    @Override
    public void update() {
        System.out.println("\tUpdating Attack!");
        //If no path has been created yet, then we better get onto recalculating
        boolean requiresRecalculation = (currentPathToTarget == null);
        boolean canAttackTarget = false;

        if(target.isDead()){
            target = null;
        }

        if(target != null && actor.getVisibleCells().stream().anyMatch(basicCell -> basicCell == target.getContainingCell())){
            lastLocationOfTarget = target.getContainingCell();
        }

        //Check if the target exists and is still visible, otherwise we give up on attacking and switch to exploring last location of target
        if (target == null || actor.getVisibleCells().stream().noneMatch(basicCell -> basicCell.getEntity() == target)) {
            isTargetReachable = false;
        }else if(WorldUtils.isAdjacent(target.getContainingCell(), actor.getContainingCell())){
            System.out.println("\t\tAdjacent to target!");
            canAttackTarget = true;
        }

        if(canAttackTarget){
            int xDiff = target.getX() - actor.getX();
            int yDiff = target.getY() - actor.getY();
            actor.setNewAction(new MeleeAttackAction(new AttackData(actor.getTimeToHit(), xDiff, yDiff, actor.getDamage())));
        }
        else if (isTargetReachable) {
            BasicCell targetCell = target.getContainingCell();
            //First check if the path is empty. If it is, and we haven't attacked AND the target is reachable, we gotta recalculate
            //Check if the path still leads us to being beside the target
            if (currentPathToTarget != null) {
                if(currentPathToTarget.isEmpty()){
                    System.out.println("\t\tNo path and no target not in reach");
                    requiresRecalculation = true;
                }else {
                    if(currentPathToTarget.get(currentPathToTarget.size() - 1) == targetCell){
                        currentPathToTarget.remove(currentPathToTarget.size() - 1);
                    }
                    if (!WorldUtils.isAdjacent(currentPathToTarget.get(currentPathToTarget.size() - 1), targetCell)) {
                        System.out.println("\t\tPath doesn't lead to target!");
                        requiresRecalculation = true;
                    }
                    if (!WorldUtils.isAdjacent(actor.getContainingCell(), currentPathToTarget.get(0))) {
                        System.out.println("\t\tActor no longer adjacent to path!");
                        requiresRecalculation = true;
                    }
                }
            }
            if (requiresRecalculation) {
                System.out.println("\t\tCreating Path to target:");
                currentPathToTarget = WorldUtils.getAStarPath(actor.getLevel(), actor.getContainingCell(), targetCell, actor, false);
                if (currentPathToTarget == null) {
                    System.out.println("\t\t\tNo valid path");
                    isTargetReachable = false;
                } else {
                    //Don't include the final cell, as this is the one that actually contains the target! We want to end up BESIDE the target.
                    currentPathToTarget.remove(currentPathToTarget.size() - 1);
                    System.out.println("\t\t\t" + currentPathToTarget);
                }
            }

            //The path can be null if the pathfinder was unable to find any paths.
            //This is ok, it just means the target is visible but there is no available path to it.
            if (currentPathToTarget != null) {
                System.out.println("\t\tContinuing to move phase");
                //Now we can get on moving
                BasicCell nextStep = currentPathToTarget.get(0);
                currentPathToTarget.remove(0);
                int x = nextStep.getX() - actor.getX();
                int y = nextStep.getY() - actor.getY();
                System.out.printf("\t\t(%d, %d) -> (%d, %d)\n", actor.getX(), actor.getY(), actor.getX() + x, actor.getY() + y);
                if (WorldUtils.isAdjacent(actor.getContainingCell(), nextStep)) {
                    actor.setNewAction(new MoveAction(actor.getTimeToMove(x, y), x, y));
                } else {
                    throw new Error("Attempted to move more than one cell during attack approach phase!");
                }

            }
        }
    }
}
