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

    public void updateViewRange(){
        if(!floorMemoryMap.containsKey(getFloor())){
            floorMemoryMap.put(getFloor(), new FloorMemory(getFloor()));
        }

        FloorMemory currentFloorMemory = floorMemoryMap.get(getFloor());


        //***********
        //Update Visual Memory
        //***********
        Set<BasicCell> viewableCells = new HashSet<>();

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

            List<Point2D[]> lines = new ArrayList<>();
            for (int i = 0; i < rays; i++) {
                float currentAngle = angleDiv * i;
                float dx = (float) Math.cos(currentAngle);
                float dy = (float) Math.sin(currentAngle);
                float x = getPosX();
                float y = getPosY();
//          System.out.printf("Start : (%f, %f)\n", x, y);
                Point2D start = new Point2D.Float(x, y);

                while (true) {
                    //The values of the next borders to be intersected
                    int nextXIntersect = 0;
                    int nextYIntersect = 0;
                    boolean horizontal = false;
                    boolean vertical = false;

                    if (dx == 0) {
                        vertical = true;
                    } else {
                        //Calculate by rounding up or down depending on direction
                        nextXIntersect = (int) ((dx > 0) ? Math.ceil(x) : Math.floor(x));
                    }
                    if (dy == 0) {
                        horizontal = true;
                    } else {
                        //Calculate by rounding up or down depending on direction
                        nextYIntersect = (int) ((dy > 0) ? Math.ceil(y) : Math.floor(y));
                    }

                    float minStepsToIntersect;
                    if (horizontal && vertical) {
                        throw new RuntimeException("Ray cast was not vertical or horizontal");
                    } else if (horizontal) {
                        minStepsToIntersect = (nextXIntersect - x) / dx;
                    } else if (vertical) {
                        minStepsToIntersect = (nextYIntersect - y) / dy;
                    } else {
                        minStepsToIntersect = Math.min((nextXIntersect - x) / dx, (nextYIntersect - y) / dy);
                    }

                    //To ensure we're in the next block, add a delta to hop over the intersection
                    // Decrease the second term if cells are being skipped over corners
                    float stepsToNextIntersect = minStepsToIntersect + 0.01f;

                    float nextX = x + dx * stepsToNextIntersect;
                    float nextY = y + dy * stepsToNextIntersect;
                    int nextBlockX = (int) Math.floor(nextX);
                    int nextBlockY = (int) Math.floor(nextY);

                    //Check if the distance of this ray now exceeds max radius
                    float xDist = nextX - getPosX();
                    float yDist = nextY - getPosY();
                    float squaredDist = xDist * xDist + yDist * yDist;
                    boolean exceedsRange = squaredDist > sightRange * sightRange;

                    BasicCell nextCell = getContainingCell().getFloor().getCellAt(nextBlockX, nextBlockY);

                    if (nextCell == null || nextCell == getContainingCell() || exceedsRange) {
                        Point2D end = new Point2D.Float(nextX, nextY);
                        lines.add(new Point2D[]{start, end});
                        //End a rayline without saving last cell
                        break;

                    } else {
                        viewableCells.add(nextCell);
                        x = nextX;
                        y = nextY;
                        if (!nextCell.canBeSeenThrough(this)) {
                            //Even though the block can't be seen /through/ it should still be visible!
                            Point2D end = new Point2D.Float(nextX, nextY);
                            lines.add(new Point2D[]{start, end});
                            break;
                        }
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
                CellMemory data = new CellMemory(VisualProcessor.getVisual(currentCell, this), fData,(currentCell.canBeEntered(this) ? CellMemory.EnterableStatus.OPEN : CellMemory.EnterableStatus.CLOSED), currentCell.getX(), currentCell.getY(), isCreatureWithinThisCell);
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
