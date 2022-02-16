package com.ewan.dunjeon.world.entities;

import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.ai.GenericTask;
import com.ewan.dunjeon.world.entities.ai.SearchGivenArea;

import java.awt.*;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class Monster extends Entity{
    public Monster(Color c, Predicate<Entity> predicate) {
        super(c, 3, 5);
        isTargetPredicate = predicate;
    }

    GenericTask task;
    Predicate<Entity> isTargetPredicate;

    @Override
    public void update() {
        super.update();
        checkViewForTargets();
        if(task == null){
            task = SearchGivenArea.RandomSearchRememberedArea(this);
        }
        task.update();

    }

    public void checkViewForTargets(){
        Set<BasicCell> visibleCells = getVisibleCells();
        Set<BasicCell> possibleTargets = visibleCells.stream().filter(basicCell -> isTargetPredicate.test(basicCell.getEntity())).collect(Collectors.toSet());
    }


}
