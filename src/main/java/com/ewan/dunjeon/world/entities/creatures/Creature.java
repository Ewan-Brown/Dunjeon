package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.game.Main;
import com.ewan.dunjeon.world.Interactable;
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
import java.util.stream.Collectors;

public abstract class Creature extends Entity {
    public Creature(Color c, String name) {
        super(c, name);
        sightRange = 5;
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

    enum AxisAlignment {
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

                while (true) {
                    //The values of the next borders to be intersected
                    int nextVerticalBorderIntersect = 0;
                    int nextHorizontalBorderIntersect = 0;
                    AxisAlignment rayDirection = null;
                    AxisAlignment borderIntersectDirection = null;

                    if (dx == 0) {
                        rayDirection = AxisAlignment.VERTICAL;
                    } else {
                        //Calculate by rounding up or down depending on direction
                        nextVerticalBorderIntersect = (int) ((dx > 0) ? Math.ceil(x) : Math.floor(x));
                    }
                    if (dy == 0) {
                        rayDirection = AxisAlignment.HORIZONTAL;
                    } else {
                        //Calculate by rounding up or down depending on direction
                        nextHorizontalBorderIntersect = (int) ((dy > 0) ? Math.ceil(y) : Math.floor(y));
                    }

                    if(dx != 0 && dy != 0){
                        rayDirection = AxisAlignment.DIAGONAL;
                    }



                    float minStepsToIntersect = switch (rayDirection){
                        case HORIZONTAL -> (nextVerticalBorderIntersect - x) / dx;
                        case VERTICAL -> (nextHorizontalBorderIntersect - y) / dy;
                        case DIAGONAL -> Math.min((nextVerticalBorderIntersect - x) / dx, (nextHorizontalBorderIntersect - y) / dy); //Don't worry sign is preserved :)
                    };

                    borderIntersectDirection = switch (rayDirection){
                        case HORIZONTAL -> AxisAlignment.VERTICAL;
                        case VERTICAL -> AxisAlignment.HORIZONTAL;
                        case DIAGONAL -> ((nextVerticalBorderIntersect - x) / dx < (nextHorizontalBorderIntersect - y) / dy) ? AxisAlignment.VERTICAL : AxisAlignment.HORIZONTAL;
                    };

                    int currentBlockX = (int)Math.floor(x);
                    int currentBlockY = (int)Math.floor(y);

                    int nextBlockX2 = 0;
                    int nextBlockY2 = 0;

                    if(borderIntersectDirection == AxisAlignment.HORIZONTAL){
                        int ySignum = (int)Math.signum(dy);

                        if(ySignum == -1){
                            nextBlockY2 = currentBlockY - 1;
                            nextBlockX2 = currentBlockX;
                        }else if(ySignum == 1){
                            nextBlockY2 = currentBlockY + 1;
                            nextBlockX2 = currentBlockX;
                        }else{
                            throw new IllegalStateException();
                        }
                    }else if(borderIntersectDirection == AxisAlignment.VERTICAL){
                        int xSignum = (int)Math.signum(dx);

                        if(xSignum == -1){
                            nextBlockX2 = currentBlockX - 1;
                            nextBlockY2 = currentBlockY;
                        }else if(xSignum == 1){
                            nextBlockX2 = currentBlockX + 1;
                            nextBlockY2 = currentBlockY;
                        }else{
                            throw new IllegalStateException();
                        }
                    }

                    //To ensure we're in the next block, add a delta to hop over the intersection
                    // Decrease the second term if cells are being skipped over corners

                    float stepsToNextIntersect = minStepsToIntersect + 0.0001f;

                    float nextX = x + dx * stepsToNextIntersect;
                    float nextY = y + dy * stepsToNextIntersect;
                    int nextBlockX = (int) Math.floor(nextX);
                    int nextBlockY = (int) Math.floor(nextY);

                    System.out.println(borderIntersectDirection);
                    System.out.println(nextBlockX - nextBlockX2);
                    System.out.println(nextBlockY - nextBlockY2);
                    System.out.println();

                    nextBlockY = nextBlockY2;
                    nextBlockX = nextBlockX2;

                    //Check if the distance of this ray now exceeds max radius
                    float xDist = nextX - getPosX();
                    float yDist = nextY - getPosY();
                    float squaredDist = xDist * xDist + yDist * yDist;
                    boolean exceedsRange = squaredDist > sightRange * sightRange;

                    BasicCell nextCell = getContainingCell().getFloor().getCellAt(nextBlockX, nextBlockY);

                    if (nextCell == null || nextCell == getContainingCell() || exceedsRange) {
                        break;

                    } else {
                        viewableCells.add(nextCell);

                        if(!nextCell.canBeSeenThrough(this)){
                            //Figure out what wall this collides with

                            float intersectX = x + dx * minStepsToIntersect;
                            float intersectY = y + dy * minStepsToIntersect;

                            float yDiff = intersectY - (nextBlockY + 0.5f);
                            float xDiff = intersectX - (nextBlockX + 0.5f);

                            float angle = (float)Math.atan2(yDiff, xDiff);

                            if(angle < 0){
                                angle += (float)Math.PI*2;
                            }

                            float quarterPI = (float)Math.PI/4f;

                            BasicCell.CellSide visibleSide = null;

                            if(angle > quarterPI * 7 || angle < quarterPI * 1){
                                visibleSide = BasicCell.CellSide.EAST;
                            }else if(angle < quarterPI * 3){
                                visibleSide = BasicCell.CellSide.SOUTH;
                            }else if(angle < quarterPI * 5){
                                visibleSide = BasicCell.CellSide.WEST;
                            }else if(angle < quarterPI * 7){
                                visibleSide = BasicCell.CellSide.NORTH;
                            }

                            if(viewableWalls.containsKey(nextCell)){
                                viewableWalls.get(nextCell).add(visibleSide);
                            }else{
                                viewableWalls.put(nextCell, new ArrayList<>(Collections.singleton(visibleSide)));
                            }
                            break;
                        }
                        x = nextX;
                        y = nextY;
                    }
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
            //Iterate through entities who are in cell within view range. May miss some entities?

            for (Entity entity : getFloor().getEntities().stream().filter(entity -> entity != this && viewableCells.contains(entity.getContainingCell())).collect(Collectors.toList())) {
                boolean chattable = false;
                if(chatInteractive == entity){
                    chattable = true;
                }
                EntityMemory entityMemory = new EntityMemory(entity.getUUID(), entity.getPosX(), entity.getPosY(), entity.getVelX(), entity.getVelY(), entity.getSize(), chattable, VisualProcessor.getVisual(entity, this));
                currentFloorMemory.updateEntity(entity.getUUID(), entityMemory);
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
