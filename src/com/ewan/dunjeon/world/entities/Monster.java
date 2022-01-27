package com.ewan.dunjeon.world.entities;

import com.ewan.dunjeon.generation.Main;
import com.ewan.dunjeon.graphics.LiveDisplay;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.ai.actions.MoveAction;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class Monster extends Entity{
    public Monster(Color c) {
        super(c, 3, 3);
        currentPath = null;
    }

    List<BasicCell> currentPath;
    List<BasicCell> inVision = new ArrayList<>();
    int maxRange = 12;
    int minRange = 8;

    @Override
    public void update() {
        super.update();
        if (getCurrentAction() == null) {
            randomSearch();
        }

    }

    public void randomSearch(){
        //If the current path is either immediately blocked or finished, generate a new one
        if (currentPath == null  || currentPath.size() == 0 || !currentPath.get(0).canBeEntered(this)) {

            //Find an eligible target cell for exploration
            List<BasicCell> validCells = this.containingCell.getLevel().getCellsAsList().stream().filter(basicCell -> {
                float dist = WorldUtils.getRawDistance(basicCell, Monster.this.containingCell);
                return basicCell.canBeEntered(Monster.this)  //Check that target cell can even be entered
                        && dist < maxRange //Check the distance between this entity's cell and the target cell
                        && dist > minRange
                        && basicCell != Monster.this.containingCell;
            }).collect(Collectors.toList());

            //Check if that cell is even accessible by slime
            List<List<BasicCell>> validPaths = validCells.stream()
                    .map(basicCell -> WorldUtils.getAStarPath(Monster.this.getLevel(), Monster.this.containingCell, basicCell, Monster.this, true))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            //Choose one of these paths at random
            currentPath = validPaths.get(Main.rand.nextInt(validPaths.size()));
        }
        System.out.println("Current Cell : " + containingCell);
        BasicCell nextCell = currentPath.get(0);
        System.out.println("Next Cell : " + nextCell);
        int x = nextCell.getX() - containingCell.getX();
        int y = nextCell.getY() - containingCell.getY();
        currentPath.remove(0);
        setNewAction(new MoveAction(getSpeed(), x, y));
    }
}
