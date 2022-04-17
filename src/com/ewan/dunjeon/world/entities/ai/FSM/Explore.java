package com.ewan.dunjeon.world.entities.ai.FSM;

import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.entities.Monster;
import com.ewan.dunjeon.world.entities.actions.MoveAction;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.ewan.dunjeon.game.TestGameLogic.rand;

public class Explore extends State{

    int restCounter; // Representative of how 'rested' the entity is. Higher = can explore longer. At 0 the entity must rest.

    boolean resting;
    static final int MAX_REST = 10;

    public Explore(Monster a){
        super(a);
        restCounter = MAX_REST;
        resting = false;
    }

    @Override
    public State getNextState() {
        List<BasicCell> possibleTargets = actor.getVisibleCells().stream().filter(basicCell -> actor.getTargetPredicate().test(basicCell.getEntity())).collect(Collectors.toList());
        if(!possibleTargets.isEmpty()){
            System.out.println("\tPossible targets found!");
            Entity t = possibleTargets.get(rand.nextInt(possibleTargets.size())).getEntity();
            return new Attack(actor, t);
        }else {
            return null;
        }
    }

    Point[] directions = new Point[]{new Point(1, 0), new Point(-1, 0), new Point(0, 1), new Point(0, -1)};

    @Override
    public void update() {
        System.out.println("\tUpdating Exploration!");
        if (resting) {
            System.out.println("\t\tResting");
            restCounter++;
            //This should mean that he continues rest until he's half rested,
            // then the chance of cancelling rest increases linearly as rest increases. should be 100% chance to explore if rest is at max rest
            if (Math.max(0, restCounter - MAX_REST / 2) > rand.nextInt(MAX_REST / 2)) { //TODO Check that this does what it should
                resting = false;
            }
        } else {
//            System.out.println("\t\tExploring!");
            restCounter--;
            if (restCounter < 0) {
//                System.out.println("\t\t Too tired.. Time to rest!");
                resting = true;
            } else {
                Point direction = directions[rand.nextInt(directions.length)];
                int x = (int) direction.getX();
                int y = (int) direction.getY();
                actor.setNewAction(new MoveAction(actor.getTimeToMove(x, y), x, y));
            }
        }
    }
}
