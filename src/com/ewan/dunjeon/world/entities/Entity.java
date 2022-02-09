package com.ewan.dunjeon.world.entities;

import com.ewan.dunjeon.world.ItemHolder;
import com.ewan.dunjeon.world.level.Level;
import com.ewan.dunjeon.world.Updateable;
import com.ewan.dunjeon.world.entities.actions.GenericAction;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.item.Item;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Entity implements ItemHolder, Updateable {
    public BasicCell containingCell;
    private GenericAction currentAction = null;
    private int speed;
    protected int sightRange;
    private Set<BasicCell> lastVisibleCells = new HashSet<>();
    private Set<BasicCell> rememberedCells = new HashSet<>();
    Color color;

    public Entity(Color c, int s, int sight){
        this.color = c;
        speed = s;
        sightRange = sight;
    }

    public Set<BasicCell> lastVisibleCells(){
        return lastVisibleCells;
    }

    public Level getLevel(){
        return containingCell.getLevel();
    }

    public void enterCell(BasicCell c){
        containingCell = c;
    }

    @Override
    public List<Item> getItems() {
        return null;
    }

    public Color getColor(){
        return color;
    }

    public int getX(){
        return containingCell.getX();
    }
    public int getY(){
        return containingCell.getY();
    }
    public int getSpeed(){
        return speed;
    }

    public void setNewAction(GenericAction a){
        if(currentAction != null){
            currentAction.cancel();
        }
        if(a == null){
            currentAction = null;
        }else{
            a.setActor(this);
            currentAction = a;
        }

    }

    public Set<BasicCell> getVisibleCells(){
        return lastVisibleCells;
    }

    public Set<BasicCell> getRememberedCells(){
        return rememberedCells;
    }

    public void updateMemory(){
        rememberedCells.addAll(lastVisibleCells);
    }

    public void updateViewRange(){
        Set<BasicCell> viewableCells = new HashSet<>();

        //Use enough rays that we don't skip over whole cells.
        //Arc length : r = al
        //  Where r is arc length, a is angle, l is length
        float arcLength = 0.1f;
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
//          System.out.printf("Start : (%f, %f)\n", x, y);
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

                //Check if the distance of this ray now exceeds max radius
                float xDist = nextX - originX;
                float yDist = nextY - originY;
                float squaredDist = xDist*xDist + yDist*yDist;
                boolean exceedsRange = squaredDist > sightRange*sightRange;

                BasicCell nextCell = containingCell.getLevel().getCellAt(nextBlockX, nextBlockY);

                if(nextCell == null || nextCell == containingCell || exceedsRange){
                    Point2D end = new Point2D.Float(nextX,nextY);
                    lines.add(new Point2D[]{start, end});
                    //End a rayline without saving last cell
                    break;

                }else{
                    //Save this cell
                    if(!viewableCells.contains(nextCell)) {
                        viewableCells.add(nextCell);
                    }
                    x = nextX;
                    y = nextY;
                    if(!nextCell.canBeSeenThrough(this)){
                        //Saved this cell but it's the end of the rayline
                        Point2D end = new Point2D.Float(nextX,nextY);
                        lines.add(new Point2D[]{start, end});
                        break;
                    }
                }
            }
        }
        lastVisibleCells = viewableCells;
//        LiveDisplay.setDebugLines(lines);
    }

    @Override
    public void update() {
        //Update the visible cell range for outer usage
        updateViewRange();
        updateMemory();
        if(currentAction != null){
            currentAction.update();
            if(currentAction.isDone()) {
                currentAction = null;
            }
        }
    }

    public GenericAction getCurrentAction(){
        return currentAction;
    }

    public void onDeath(){

    }

}
