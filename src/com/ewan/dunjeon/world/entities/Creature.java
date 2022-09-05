package com.ewan.dunjeon.world.entities;

import com.ewan.dunjeon.game.Main;
import com.ewan.dunjeon.world.sounds.AbsoluteSoundEvent;
import com.ewan.dunjeon.world.World;
import com.ewan.dunjeon.world.cells.BasicCell;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Creature extends Entity{
    public Creature(Color c, String name) {
        super(c, name);
        sightRange = 5;
        health = 10;
    }

    private int sightRange;
    private int health;

    private Set<BasicCell> lastVisibleCells = new HashSet<>();
    private Set<BasicCell> rememberedCells = new HashSet<>();
    private double loudStepChance = 0.001d; // TODO Do fun stuff with this!


    @Override
    public void update() {
        super.update();

        updateViewRange();
        updateMemory();
        if(getVelX() != 0 || getVelY() != 0){
            if(Main.rand.nextDouble() < loudStepChance){
                World.getInstance().getSoundManager().exposeSound(new AbsoluteSoundEvent(5, getPoint2D(), getFloor(),"", "Something stumbles in the dark", AbsoluteSoundEvent.SoundType.PHYSICAL, this));
            }
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

        viewableCells.add(this.getContainingCell()); // DUe to some foresight this has to be added?

        List<Point2D[]> lines = new ArrayList<>();
        for(int i = 0; i < rays; i++){
            float currentAngle = angleDiv * i;
            float dx = (float)Math.cos(currentAngle);
            float dy = (float)Math.sin(currentAngle);
            float x = getPosX();
            float y = getPosY();
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
                float xDist = nextX - getPosX();
                float yDist = nextY - getPosY();
                float squaredDist = xDist*xDist + yDist*yDist;
                boolean exceedsRange = squaredDist > sightRange*sightRange;

                BasicCell nextCell = getContainingCell().getFloor().getCellAt(nextBlockX, nextBlockY);

                if(nextCell == null || nextCell == getContainingCell() || exceedsRange){
                    Point2D end = new Point2D.Float(nextX,nextY);
                    lines.add(new Point2D[]{start, end});
                    //End a rayline without saving last cell
                    break;

                }else{
                    viewableCells.add(nextCell);
                    x = nextX;
                    y = nextY;
                    if(!nextCell.canBeSeenThrough(this)){
                        //Even though the block can't be see /through/ it should still be visible!
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
    public boolean exists() {
        return !isDead();
    }

    public boolean isDead(){
        return health <= 0;
    }

    public void applyDamage(int d){
        health -= d;
        if(isDead()){
            System.out.println("Entity died!");
            this.getContainingCell().onDeath(this);
        }
    }

    public Color getColor(){
        return isDead() ? Color.RED  : color;
    }

    public int getSightRange(){return sightRange;}

    public int getHealth() {
        return health;
    }

    public Set<BasicCell> lastVisibleCells(){
        return lastVisibleCells;
    }

    public Point2D getPoint2D(){return new Point2D.Double(getPosX(), getPosY());}

}
