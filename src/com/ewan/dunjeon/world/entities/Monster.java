package com.ewan.dunjeon.world.entities;

import com.ewan.dunjeon.generation.Main;
import com.ewan.dunjeon.graphics.LiveDisplay;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.ai.MoveAction;
import com.ewan.dunjeon.world.cells.BasicCell;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

public class Monster extends Entity{
    public Monster(Color c) {
        super(c, 30);
        currentPath = null;
    }

    List<BasicCell> currentPath;
    List<BasicCell> inVision;
    int maxRange = 12;
    int minRange = 8;
    float sightRange = 100;

    @Override
    public void update() {
        super.update();
        if (getCurrentAction() == null) {
            randomSearch();
        }

    }

    /**
     * Update list of all viewable cells from this monsters' perspectives
     */
    public void scanSurroundings(){
//        System.out.println();
//        System.out.println("I'm at " + Monster.this.containingCell);
        //Only accept cells that follow this criteria:
        //  Cell can be entered by this monster
        //  Cell is not the containing cell of this monster
        //  Cell is within view distance
        //  Cell line-of-sight view is unobstructed
        //
        // (Note that duplicate cells are discarded)
//        List<BasicCell> viewableCells = getLevel().getCellsAsList().stream()
//                .filter(basicCell -> basicCell.canBeEntered(Monster.this))
//                .filter(basicCell -> !basicCell.equals(Monster.this.containingCell))
//                .filter(basicCell -> WorldUtils.getRawDistance(basicCell, Monster.this.containingCell) < Monster.this.sightRange)
//                .filter(basicCell -> WorldUtils.raytrace(Monster.this.containingCell, basicCell))
//                .distinct()
//                .collect(Collectors.toList());

//        System.out.println("viewableCells.size() = " + viewableCells.size());
//        for (BasicCell viewableCell : viewableCells) {
////            System.out.println("viewableCell = " + viewableCell);
//        }

        List<BasicCell> viewableCells = new ArrayList<>();
        int rays = 1;
        float angleDiv = 360f / rays;
        inVision = viewableCells;
    }

    public void testSight(){
        scanSurroundings();
        LiveDisplay.setSelectedCells(inVision);
    }

    public void randomSearch(){
        scanSurroundings();
        //If the current path is either immediately blocked or finished, generate a new one
//        if (currentPath == null  || currentPath.size() == 0 || !currentPath.get(0).canBeEntered(this)) {
//            //Find an eligible target cell for exploration
//
//
//            List<BasicCell> validCells = this.containingCell.getLevel().getCellsAsList().stream().filter(basicCell -> {
//                float dist = WorldUtils.getRawDistance(basicCell, Monster.this.containingCell);
//                return basicCell.canBeEntered(Monster.this)  //Check that target cell can even be entered
//                        && dist < maxRange //Check the distance between this entity's cell and the target cell
//                        && dist > minRange
//                        && basicCell != Monster.this.containingCell;
//            }).collect(Collectors.toList());
//
//
//            //Check if that cell is even accessible by slime
//            BasicCell[][] map = containingCell.getLevel().getCells();
//            List<List<BasicCell>> validPaths = validCells.stream()
//                    .map(basicCell -> WorldUtils.getAStarPath(Monster.this.getLevel(), Monster.this.containingCell, basicCell, Monster.this, false))
//                    .filter(Objects::nonNull)
//                    .collect(Collectors.toList());
//
//
//            //Choose one of these paths at random
//            currentPath = validPaths.get(Main.rand.nextInt(validPaths.size()));
//        }
//        BasicCell nextCell = currentPath.get(0);
//        int x = nextCell.getX() - containingCell.getX();
//        int y = nextCell.getY() - containingCell.getY();
//        currentPath.remove(0);
//        setNewAction(new MoveAction(getSpeed(), x, y));
    }
}
