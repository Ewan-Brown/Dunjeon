package com.ewan.dunjeon.world.entities;

import com.ewan.dunjeon.graphics.LiveDisplay;
import com.ewan.dunjeon.world.cells.BasicCell;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Monster extends Entity{
    public Monster(Color c) {
        super(c, 30);
        currentPath = null;
    }

    List<BasicCell> currentPath;
    List<BasicCell> inVision = new ArrayList<>();
    int maxRange = 12;
    int minRange = 8;
    float sightRange = 30;

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
        inVision.clear();
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

        //Use enough rays that we don't skip over whole cells.
        //Arc length : r = al
        //  Where r is arc length, a is angle, l is length
        float arcLength = 1;
        float angleDiv =  arcLength/sightRange;
        int rays = (int)Math.ceil(2 * (float)Math.PI / angleDiv);

        float originX = containingCell.getX();
        float originY = containingCell.getY();

        List<Point2D[]> lines = new ArrayList<>();
        for(int i = 0; i < rays; i++){
            float currentAngle = angleDiv * i;
            float dx = (float)Math.cos(currentAngle);
            float dy = (float)Math.sin(currentAngle);
            float x = originX + 0.5f;
            float y = originY + 0.5f;
//            System.out.printf("Start : (%f, %f)\n", x, y);
            Point2D start = new Point2D.Float(x,y);
            while(true){
                //The values of the next borders to be intersected
                int nextXIntersect = 0;
                int nextYIntersect = 0;
                boolean horizontal = false;
                boolean vertical = false;

                if(dx == 0){
                    vertical = true;
                }
                else{
                    //Calculate by rounding up or down depending on direction
                    nextXIntersect = (int)((dx > 0) ? Math.ceil(x) : Math.floor(x));
                }
                if(dy == 0){
                    horizontal = true;
                }else{
                    //Calculate by rounding up or down depending on direction
                    nextYIntersect = (int)((dy > 0) ? Math.ceil(y) : Math.floor(y));
                }

                float minStepsToIntersect;
                if(horizontal && vertical){
                    throw new RuntimeException("Ray cast was not vertical or horizontal");
                }else if(horizontal) {
                    minStepsToIntersect = (nextXIntersect - x) / dx;
                }else if(vertical){
                    minStepsToIntersect = (nextYIntersect - y) / dy;
                }else {
                    minStepsToIntersect = Math.min((nextXIntersect - x) / dx, (nextYIntersect - y) / dy);
                }

                //To ensure we're in the next block, add a delta to hop over the intersection
                // Decrease the second term if cells are being skipped over corners
                float stepsToNextIntersect = minStepsToIntersect + 0.01f;

                float nextX = x + dx * stepsToNextIntersect;
                float nextY = y + dy * stepsToNextIntersect;
                int nextBlockX = (int)Math.floor(nextX);
                int nextBlockY = (int)Math.floor(nextY);
//                System.out.printf("(%f, %f)\n", nextX, nextY);

                //Check if the distance of this ray now exceeds max radius
                float xDist = nextX - originX;
                float yDist = nextY - originY;
                float squaredDist = xDist*xDist + yDist*yDist;
                boolean exceedsRange = squaredDist > sightRange*sightRange;

//                System.out.printf("x/y dist (%f, %f)\n", xDist, yDist);
//                System.out.println("squaredDist : " + squaredDist);
//                System.out.println(exceedsRange);

                BasicCell nextCell = containingCell.getLevel().getCellAt(nextBlockX, nextBlockY);

                if(nextCell == null || nextCell == containingCell || exceedsRange){
                    Point2D end = new Point2D.Float(nextX,nextY);
                    lines.add(new Point2D[]{start, end});
                    break;

                }else{
                    viewableCells.add(nextCell);
                    x = nextX;
                    y = nextY;
                    if(!nextCell.canBeSeenThrough(this)){
                        Point2D end = new Point2D.Float(nextX,nextY);
                        lines.add(new Point2D[]{start, end});
                        break;
                    }
                }

            }
        }
        inVision = viewableCells.stream().distinct().collect(Collectors.toList());
        LiveDisplay.setHighlightedCells(inVision);
        LiveDisplay.setLinesToDraw(lines);
    }

    public void testSight(){
        scanSurroundings();
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
