package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.game.Main;
import com.ewan.dunjeon.world.Interactable;
import com.ewan.dunjeon.world.Pair;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.cells.VisualProcessor;
import com.ewan.dunjeon.world.entities.ItemAsEntity;
import com.ewan.dunjeon.world.entities.memory.CellMemory;
import com.ewan.dunjeon.world.entities.memory.EntityMemory;
import com.ewan.dunjeon.world.entities.memory.FloorMemory;
import com.ewan.dunjeon.world.entities.memory.SoundMemory;
import com.ewan.dunjeon.world.furniture.Furniture;
import com.ewan.dunjeon.world.items.HasInventory;
import com.ewan.dunjeon.world.items.Inventory;
import com.ewan.dunjeon.world.items.Item;
import com.ewan.dunjeon.world.level.Floor;
import com.ewan.dunjeon.world.sounds.AbsoluteSoundEvent;
import com.ewan.dunjeon.world.World;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.sounds.RelativeSoundEvent;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class Creature extends Entity implements HasInventory {
    public Creature(Color c, String name) {
        super(c, name);
        sightRange = 10;
        health = 10;
        friction = 2;
    }

    private float walkSpeed = 0.03f;
    private int sightRange;
    private int health;
    private Inventory inventory = new Inventory();
    protected boolean autoPickup = false;
    private float pickupRange = 1;

    public boolean true_sight_debug = false;

    private HashMap<Floor, FloorMemory> floorMemoryMap = new HashMap();
    private double loudStepChance = 0.001d; // Just for testing sound system - can be moved somewhere else :)

    @Override
    public void update() {
        super.update();

        if(autoPickup){
            pickupItemsInVicinity();
        }
        updateViewRange();

        //Just here as an example for generating sounds
        if(getVelX() != 0 || getVelY() != 0){
            if(Main.rand.nextDouble() < loudStepChance){
                World.getInstance().getSoundManager().exposeSound(new AbsoluteSoundEvent(5, getPoint2DLoc(), getFloor(),"", "You hear a loud footstep", AbsoluteSoundEvent.SoundType.PHYSICAL, this));
            }
        }
    }

    private void pickupItemsInVicinity(){
        List<ItemAsEntity> itemEntitiesInVicinity = getFloor().getEntities().stream().filter(entity -> entity instanceof ItemAsEntity && WorldUtils.getRawDistance(Creature.this, entity) < pickupRange)
                .map((i) -> (ItemAsEntity) i).toList();

        for (ItemAsEntity itemAsEntity : itemEntitiesInVicinity) {
            Item wrappedItem = itemAsEntity.getItem();
            this.getInventory().addItem(wrappedItem);
            getFloor().removeEntity(itemAsEntity);
            this.onPickupItem(wrappedItem);
            wrappedItem.onPickUp(this);
        }

    }

    protected void onPickupItem(Item i){ }

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
        HashMap<BasicCell, List<WorldUtils.Side>> viewableWalls = new HashMap<>();

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
                List<Pair<Point, WorldUtils.Side>> intersectedTiles = WorldUtils.getIntersectedTilesWithWall(this.getPosX(), this.getPosY(),
                        this.getPosX() + (float)Math.cos(currentAngle) * sightRange, this.getPosY() + (float)Math.sin(currentAngle) * sightRange);

                for (Pair<Point, WorldUtils.Side> pair : intersectedTiles) {

                    Point intersectedPoint = pair.getElement0();
                    WorldUtils.Side intersectedSide = pair.getElement1();

                    BasicCell cell = getFloor().getCellAt(intersectedPoint);

                    if(cell == null){
                        break;
                    }
                    viewableCells.add(cell);
                    if(!cell.canBeSeenThrough(this)){
//
                        if(cell.isFilled()){
                            if (viewableWalls.containsKey(cell)) {
                                    viewableWalls.get(cell).add(intersectedSide);
                                } else {
                                    viewableWalls.put(cell, new ArrayList<>(Collections.singleton(intersectedSide)));
                                }
                        }
                        break;
                    }
                }

                //TODO Reuse WorldUtils.getIntersectingTiles here. This was the prototype and can be mostly removed.
//
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
                    if(cellMemories.stream().noneMatch(cellMemory -> cellMemory == null || cellMemory.enterable == CellMemory.EnterableStatus.CLOSED) || true_sight_debug) {
                        boolean chattable = (chatInteractive == entity);
                        EntityMemory entityMemory = new EntityMemory(entity.getUUID(), entity.getPosX(), entity.getPosY(), entity.getVelX(), entity.getVelY(), entity.getSize(), chattable, VisualProcessor.getVisual(entity, this));
                        currentFloorMemory.updateEntity(entity.getUUID(), entityMemory);
                    }
                }
            }

        }
    }

    public void onSoundEvent(RelativeSoundEvent event){
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

    public Inventory getInventory(){return inventory;}

}
