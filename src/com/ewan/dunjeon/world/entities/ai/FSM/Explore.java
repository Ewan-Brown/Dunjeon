package com.ewan.dunjeon.world.entities.ai.FSM;

import com.ewan.dunjeon.game.TestGameLogic;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.entities.Monster;
import com.ewan.dunjeon.world.entities.actions.MoveAction;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ewan.dunjeon.game.TestGameLogic.rand;

public class Explore extends State{

    List<BasicCell> path = null;

    public Explore(Monster a){
        super(a);
    }

    public Explore(Monster a, BasicCell initialExplore){
        this(a);
        path = WorldUtils.getAStarPath(a.getLevel(), a.getContainingCell(), initialExplore, a, false);
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

    public boolean requiresRecalculation(){

        if(path == null){
            return true;
        }else {

            if (path.isEmpty()) {
                System.out.println("Explore - Path is empty");
                return true;
            }

            if (path.stream().anyMatch(basicCell -> !basicCell.canBeEntered(actor))) {
                System.out.println("Explore - Path is not valid, unenterable cell");
                return true;
            }
            BasicCell nextCell = path.get(0);
            if (nextCell == actor.getContainingCell()) {
                System.out.println("Explore - Path not valid, nextCell = actor's current cell ");
                return true;
            }
            if(!WorldUtils.isAdjacent(nextCell, actor.getContainingCell())){
                System.out.println("Explore - Path not valid, next cell is NOT adjacent to actor's current cell");
            }
        }

        return false;
    }

    @Override
    public void update() {

        if(requiresRecalculation()){
            path = null;
            System.out.println("Recalculating path");
            List<List<BasicCell>> validPaths = actor.getVisibleCells().stream()
                    .filter(basicCell -> basicCell != actor.getContainingCell())
                    .map(basicCell -> WorldUtils.getAStarPath(actor.getLevel(), actor.getContainingCell(), basicCell, actor, false))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if(!validPaths.isEmpty()){
                System.out.println("Found a new path!");
                path = validPaths.get(rand.nextInt(validPaths.size()));
            }else{
                System.out.println("No valid paths generated");
            }
        }

        BasicCell nextCell = path.get(0);
        int x = nextCell.getX() - actor.getContainingCell().getX();
        int y = nextCell.getY() - actor.getContainingCell().getY();
        path.remove(0);
        int timeToMove = (int) (actor.getSpeed() * ((x != 0 && y != 0) ? 1.41 : 1));
        actor.setNewAction(new MoveAction(timeToMove, x, y));
    }
}
