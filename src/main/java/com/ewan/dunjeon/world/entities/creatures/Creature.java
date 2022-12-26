package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.game.Main;
import com.ewan.dunjeon.world.Interactable;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.cells.VisualProcessor;
import com.ewan.dunjeon.world.entities.memory.CellMemory;
import com.ewan.dunjeon.world.entities.memory.EntityMemory;
import com.ewan.dunjeon.world.entities.memory.FloorMemory;
import com.ewan.dunjeon.world.entities.memory.SoundMemory;
import com.ewan.dunjeon.world.furniture.Furniture;
import com.ewan.dunjeon.world.level.Floor;
import com.ewan.dunjeon.world.sounds.AbsoluteSoundEvent;
import com.ewan.dunjeon.world.World;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.sounds.RelativeSoundEvent;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class Creature extends Entity {
    public Creature(Color c, String name) {
        super(c, name);
        sightRange = 10;
        health = 10;
        friction = 2;
    }

    private float walkSpeed = 0.03f;
    private int sightRange;
    private int health;

    public boolean true_sight_debug = false;

    private HashMap<Floor, FloorMemory> floorMemoryMap = new HashMap();
    private double loudStepChance = 0.001d; // Just for testing sound system - can be moved somewhere else :)

    @Override
    public void update() {
        super.update();


        updateViewRange();
        if(getVelX() != 0 || getVelY() != 0){
            if(Main.rand.nextDouble() < loudStepChance){
                World.getInstance().getSoundManager().exposeSound(new AbsoluteSoundEvent(5, getPoint2DLoc(), getFloor(),"", "You hear a loud footstep", AbsoluteSoundEvent.SoundType.PHYSICAL, this));
            }
        }
    }



    public FloorMemory getCurrentFloorMemory(){return getFloorMemory(getFloor());}

    public FloorMemory getFloorMemory(Floor f){
        return floorMemoryMap.get(f);
    }

    public enum AxisAlignment {
        VERTICAL,
        HORIZONTAL,
        DIAGONAL
    }

    public void updateViewRange(){
        if(!floorMemoryMap.containsKey(getFloor())){
            floorMemoryMap.put(getFloor(), new FloorMemory(getFloor()));
        }

        FloorMemory currentFloorMemory = floorMemoryMap.get(getFloor());


        //***********
        //Update Visual Memory
        //***********
        Set<BasicCell> viewableCells = new HashSet<>();
        HashMap<BasicCell, List<BasicCell.CellSide>> viewableWalls = new HashMap<>();

        //Use enough rays that we don't skip over whole cells.
        //Arc length : r = al
        //  Where r is arc length, a is angle, l is length
        float arcLength = 0.1f;
        float angleDiv =  arcLength/sightRange;
        int rays = (int)Math.ceil(2 * (float)Math.PI / angleDiv);

        viewableCells.add(this.getContainingCell());

        if(true_sight_debug){
            viewableCells.addAll(getFloor().getCellsAsList());
        }
        else {

            for (int i = 0; i < rays; i++) {
                float currentAngle = angleDiv * i;
                float dx = (float) Math.cos(currentAngle);
                float dy = (float) Math.sin(currentAngle);

                float x = getPosX();
                float y = getPosY();

                int currentBlockX = (int)Math.floor(x);
                int currentBlockY = (int)Math.floor(y);

                float slope = (float) Math.tan(currentAngle);
                float b = y - slope*x;


                BasicCell previousCell = null;

                //TODO Reuse WorldUtils.getIntersectingTiles here. This was the prototype and can be mostly removed.
                while (true) {

                    //The values of the next borders to be intersected
                    int nextVerticalBorderIntersect = Integer.MAX_VALUE;
                    int nextHorizontalBorderIntersect = Integer.MAX_VALUE;


                    if (dx == 0) {
                    } else {
                        //Check if x is an exact integer
                        if(Math.ceil(x) == x){
                            nextVerticalBorderIntersect = (int) (x + Math.signum(dx));
                        }else {
                            nextVerticalBorderIntersect = (int) ((dx > 0) ? Math.ceil(x) : Math.floor(x));
                        }
                    }
                    if (dy == 0) {
                    } else {
                        //Check if y is an exact integer
                        if(Math.ceil(y) == y){
                            nextHorizontalBorderIntersect = (int) (y + Math.signum(dy));
                        }else {
                            nextHorizontalBorderIntersect = (int) ((dy > 0) ? Math.ceil(y) : Math.floor(y));
                        }
                    }

                    float stepsToNextHorizontalBorderIntersect = (nextHorizontalBorderIntersect - y) / dy;
                    float stepsToNextVerticalBorderIntersect = (nextVerticalBorderIntersect - x) / dx;

                    AxisAlignment borderIntersectionDirection = (stepsToNextHorizontalBorderIntersect < stepsToNextVerticalBorderIntersect) ? AxisAlignment.HORIZONTAL : AxisAlignment.VERTICAL;

                    float nextIntersectX;
                    float nextIntersectY;
                    int nextBlockX;
                    int nextBlockY;

                    if(borderIntersectionDirection == AxisAlignment.HORIZONTAL) {
                        nextIntersectY = nextHorizontalBorderIntersect;
                        nextIntersectX = (nextIntersectY - b)/slope;

                        nextBlockX = currentBlockX;
                        nextBlockY = (int)(currentBlockY + Math.signum(dy));
                    }else {
                        nextIntersectX = nextVerticalBorderIntersect;
                        nextIntersectY = slope*nextIntersectX + b;

                        nextBlockX = (int)(currentBlockX + Math.signum(dx));
                        nextBlockY = currentBlockY;
                    }

                    //Check if the distance of this ray now exceeds max radius
                    float xDist = nextBlockX - getPosX();
                    float yDist = nextBlockY - getPosY();
                    float squaredDist = xDist * xDist + yDist * yDist;
                    boolean exceedsRange = squaredDist > sightRange * sightRange;

                    BasicCell currentCell = getFloor().getCellAt(currentBlockX, currentBlockY);
                    BasicCell nextCell = getFloor().getCellAt(nextBlockX, nextBlockY);

                    if (nextCell == null || nextCell == getContainingCell() || exceedsRange) {
                        break;

                    } else {

                        viewableCells.add(nextCell);

                        if(!nextCell.canBeSeenThrough(this)){
                            //Figure out what wall this collides with

                            if(nextCell.isFilled()) {
                                float yDiff = y - (nextBlockY + 0.5f);
                                float xDiff = x - (nextBlockX + 0.5f);

                                float angle = (float) Math.atan2(yDiff, xDiff);

                                if (angle < 0) {
                                    angle += (float) Math.PI * 2;
                                }

                                float quarterPI = (float) Math.PI / 4f;

                                BasicCell.CellSide visibleSide = null;

                                if (angle > quarterPI * 7 || angle < quarterPI * 1) {
                                    visibleSide = BasicCell.CellSide.EAST;
                                } else if (angle < quarterPI * 3) {
                                    visibleSide = BasicCell.CellSide.SOUTH;
                                } else if (angle < quarterPI * 5) {
                                    visibleSide = BasicCell.CellSide.WEST;
                                } else if (angle < quarterPI * 7) {
                                    visibleSide = BasicCell.CellSide.NORTH;
                                }

                                if (viewableWalls.containsKey(nextCell)) {
                                    viewableWalls.get(nextCell).add(visibleSide);
                                } else {
                                    viewableWalls.put(nextCell, new ArrayList<>(Collections.singleton(visibleSide)));
                                }
                            }
                            break;
                        }
                        x = nextIntersectX;
                        y = nextIntersectY;
                    }
                    previousCell = currentCell;
                    currentBlockX = nextBlockX;
                    currentBlockY = nextBlockY;
                }
            }
        }

        //*************
        //Update Cell/Furniture/EntityMemory
        //*************

        synchronized (currentFloorMemory) {
            currentFloorMemory.setAllDataToOld();
            Interactable touchInteractive = World.getInstance().getPlayersNearestAvailableInteractionOfType(Interactable.InteractionType.TOUCH);
            Interactable chatInteractive = World.getInstance().getPlayersNearestAvailableInteractionOfType(Interactable.InteractionType.CHAT);
            for (BasicCell currentCell : viewableCells) {
                CellMemory.FurnitureData fData = null;
                if(currentCell.getFurniture() != null){
                    Furniture f = currentCell.getFurniture();
                    boolean interactable = false;
                    //FIXME This should not be in this class
                    if(touchInteractive == f && this instanceof Player){
                        interactable = true;
                    }
                    //FIXME Currently, furniture is "rendered" invisible if its' color is null. This is not a good practice use of null.
                    fData = new CellMemory.FurnitureData(f.getPosX(), f.getPosY(), f.getSize(), !f.isBlocking(), f.getColor() != null, interactable, VisualProcessor.getVisual(f, this));
                }
                boolean isCreatureWithinThisCell = (currentCell == this.getContainingCell());

                CellMemory data = new CellMemory(VisualProcessor.getVisual(currentCell, this, viewableWalls.get(currentCell)),
                        fData,(currentCell.canBeEntered(this) ? CellMemory.EnterableStatus.OPEN : CellMemory.EnterableStatus.CLOSED),
                        currentCell.getX(), currentCell.getY(), isCreatureWithinThisCell, viewableWalls.get(currentCell));
                currentFloorMemory.updateCell(currentCell.getX(), currentCell.getY(), data);
            }

            for (Entity entity : getFloor().getEntities()) {
                if(entity != this && (viewableCells.contains(entity.getContainingCell()) && entity.getContainingCell().canBeSeenThrough(this) || true_sight_debug)) {

                    
                    List<Point> intersectingTiles = WorldUtils.getIntersectedTiles(getPosX(), getPosY(), entity.getPosX(), entity.getPosY());
                    List<CellMemory> cellMemories = new ArrayList<>();
                    for (Point intersectingTile : intersectingTiles) {
                        cellMemories.add(getCurrentFloorMemory().getDataAt(intersectingTile));
                    }
                    if(cellMemories.stream().noneMatch(cellMemory -> cellMemory.enterable == CellMemory.EnterableStatus.CLOSED) || true_sight_debug) {
                        boolean chattable = (chatInteractive == entity);
                        EntityMemory entityMemory = new EntityMemory(entity.getUUID(), entity.getPosX(), entity.getPosY(), entity.getVelX(), entity.getVelY(), entity.getSize(), chattable, VisualProcessor.getVisual(entity, this));
                        currentFloorMemory.updateEntity(entity.getUUID(), entityMemory);
                    }
                }
            }

        }
    }

    public void processSound(RelativeSoundEvent event){
        getFloorMemory(event.abs().sourceFloor()).addSoundMemory(new SoundMemory(
                this.getPosX(),
                this.getPosY(),
                event.abs().sourceLocation().getX(),
                event.abs().sourceLocation().getY(),
                false,
                event.direction(),
                event.intensity()
        ));
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

    public float getWalkSpeed() { return walkSpeed;}

    public Point2D getPoint2DLoc(){return new Point2D.Double(getPosX(), getPosY());}

}
