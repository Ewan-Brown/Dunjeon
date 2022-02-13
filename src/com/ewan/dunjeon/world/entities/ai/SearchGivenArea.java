package com.ewan.dunjeon.world.entities.ai;

import com.ewan.dunjeon.generation.Main;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.entities.actions.IdleAction;
import com.ewan.dunjeon.world.entities.actions.MoveAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SearchGivenArea extends GenericTask{

    List<BasicCell> currentPath = new ArrayList<>();
    int minRange;
    int maxRange;
    Predicate<BasicCell> filter;

    private SearchGivenArea(Entity a, Predicate<BasicCell> f) {
        super(a);
        filter = f;
    }


    public void update() {
        //If the current path is either immediately blocked or finished, generate a new one
        if (currentPath == null || currentPath.size() == 0 || !currentPath.get(0).canBeEntered(actor)) {
            //Find an eligible target cell for exploration
            List<BasicCell> validCells = actor.containingCell.getLevel().getCellsAsList().stream().filter(filter).collect(Collectors.toList());

            //If no valid target cells are found then idle for a tick.
            if (validCells.size() == 0) {
                System.out.println("No Valid Cells");
//                actor.setNewAction(new IdleAction(1));
                return;
            }

            //Get a list of paths, one for each possible targetU
            List<List<BasicCell>> validPaths = validCells.stream()
                    .map(basicCell -> WorldUtils.getAStarPath(actor.getLevel(), actor.containingCell, basicCell, actor, false))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            //If no valid paths are found then idle for a tick.
            if (validPaths.size() == 0) {
                System.out.println("No Valid Paths");
//                actor.setNewAction(new IdleAction(1));
                return;
            }

            //Choose one of these paths at random
            currentPath = validPaths.get(Main.rand.nextInt(validPaths.size()));
        }

        //If the character is NOT already currently moving or doing something, stat moving to the next cell in path
        if(actor.getCurrentAction() == null) {
            BasicCell nextCell = currentPath.get(0);
            int x = nextCell.getX() - actor.containingCell.getX();
            int y = nextCell.getY() - actor.containingCell.getY();
            currentPath.remove(0);

            int timeToMove = (int) (actor.getSpeed() * ((x != 0 && y != 0) ? 1.41 : 1));


            actor.setNewAction(new MoveAction(timeToMove, x, y));
        }

    }
    
    public static SearchGivenArea RandomSearchVisibleArea(Entity m){
        return new SearchGivenArea(m, basicCell -> basicCell.canBeEntered(m)
                && m.getVisibleCells().contains(basicCell) //Check that the target cell is visible to this monster
                && basicCell != m.containingCell);
    }

    public static SearchGivenArea RandomSearchRememberedArea(Entity m){
        return new SearchGivenArea(m, basicCell -> basicCell.canBeEntered(m)
                && m.getRememberedCells().contains(basicCell) //Check that the target cell is remembered by this monster
                && basicCell != m.containingCell);
    }
}
