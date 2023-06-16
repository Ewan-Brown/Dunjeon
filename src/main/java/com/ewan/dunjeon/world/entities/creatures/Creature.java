package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.world.Pair;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.data.Data;
import com.ewan.dunjeon.world.data.DataStreamParameters;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.entities.ItemAsEntity;
import com.ewan.dunjeon.world.entities.creatures.senses.Sense;
import com.ewan.dunjeon.world.entities.memory.*;
import com.ewan.dunjeon.world.items.inventory.HasInventory;
import com.ewan.dunjeon.world.items.inventory.Inventory;
import com.ewan.dunjeon.world.items.Item;
import com.ewan.dunjeon.world.items.inventory.InventoryWithWieldedItem;
import com.ewan.dunjeon.world.cells.BasicCell;
import org.dyn4j.geometry.Vector2;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Creature extends Entity implements HasInventory {
    public Creature(String name) {
        super(name);
        sightRange = 10;
    }

    private double walkSpeed = 0.03f;
    private int sightRange;
    private InventoryWithWieldedItem inventory = new InventoryWithWieldedItem();
    protected boolean autoPickup = false;
    private double pickupRange = 1;

//    public boolean true_sight_debug = false;
    private Brain brain = new Brain();
    private double loudStepChance = 0.001d; // Just for testing sound system - can be moved somewhere else :)

    @Override
    public void update(double stepSize) {
        super.update(stepSize);
        if(autoPickup){
            pickupItemsInVicinity();
        }
        updateViewRange(this, getSightRange());
    }

    private void pickupItemsInVicinity(){
        List<ItemAsEntity> itemEntitiesInVicinity = getFloor().getEntities().stream().filter(entity -> entity instanceof ItemAsEntity && WorldUtils.getRawDistance(Creature.this, entity) < pickupRange)
                .map((i) -> (ItemAsEntity) i).collect(Collectors.toList());

        for (ItemAsEntity itemAsEntity : itemEntitiesInVicinity) {
            Item wrappedItem = itemAsEntity.getItem();
            this.getInventory().addItem(wrappedItem);
            getFloor().removeEntity(itemAsEntity);
            this.onPickupItem(wrappedItem);
            wrappedItem.onPickUp(this);
        }

    }

    protected void onPickupItem(Item i){ }

    public enum AxisAlignment {
        VERTICAL,
        HORIZONTAL,
        DIAGONAL
    }

    public static void updateViewRange(Creature c, int sightRange){
        //dyn4j TODO Bring this code back
//        if(!floorMemoryMap.containsKey(getFloor())){
//            floorMemoryMap.put(getFloor(), new FloorMemory(getFloor()));
//        }
//
//        FloorMemory currentFloorMemory = floorMemoryMap.get(getFloor());


        //***********
        //Update Visual Memory
        //***********
        Set<BasicCell> viewableCells = new HashSet<>();
        HashMap<BasicCell, List<WorldUtils.Side>> viewableWalls = new HashMap<>();

        //Use enough rays that we don't skip over whole cells.
        //Arc length : r = al
        //  Where r is arc length, a is angle, l is length
        double arcLength = 0.1f;
        double angleDiv =  arcLength/sightRange;
        int rays = (int)Math.ceil(2 * (double)Math.PI / angleDiv);

        viewableCells.add(c.getContainingCell());


            for (int i = 0; i < rays; i++) {
                double currentAngle = angleDiv * i;
                List<Pair<Point, WorldUtils.Side>> intersectedTiles = WorldUtils.getIntersectedTilesWithWall(c.getWorldCenter().x, c.getWorldCenter().y,
                        c.getWorldCenter().x + (double)Math.cos(currentAngle) * sightRange, c.getWorldCenter().y + (double)Math.sin(currentAngle) * sightRange);

                for (Pair<Point, WorldUtils.Side> pair : intersectedTiles) {

                    Point intersectedPoint = pair.getElement0();
                    WorldUtils.Side intersectedSide = pair.getElement1();

                    BasicCell cell = c.getFloor().getCellAt(new Vector2(intersectedPoint.getX(), intersectedPoint.getY()));

                    if(cell == null){
                        break;
                    }
                    viewableCells.add(cell);
                    if(!cell.canBeSeenThrough(c)){
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

        //*************
        //Update Cell/Furniture/EntityMemory
        //*************

//        synchronized (currentFloorMemory) {
//            currentFloorMemory.setAllDataToOld();
//            Interactable touchInteractive = Dunjeon.getInstance().getPlayersNearestAvailableInteractionOfType(Interactable.InteractionType.TOUCH);
//            Interactable chatInteractive = Dunjeon.getInstance().getPlayersNearestAvailableInteractionOfType(Interactable.InteractionType.CHAT);
//            //TODO Prepping for Dyn4J
////            for (BasicCell currentCell : viewableCells) {
////                CellMemory.FurnitureData fData = null;
////                if(currentCell.getFurniture() != null){
////                    Furniture f = currentCell.getFurniture();
////                    boolean interactable = false;
////                    //FIXME This should not be in this class
////                    if(touchInteractive == f && this instanceof Player){
////                        interactable = true;
////                    }
////                    //FIXME Currently, furniture is "rendered" invisible if its' color is null. This is not a good practice use of null.
////
////                    fData = new CellMemory.FurnitureData(f.getWorldCenter().x, f.getWorldCenter().y, f.getSize(), !f.isBlocking(), f.getColor() != null, interactable, VisualProcessor.getVisual(f, this));
////                }
////                boolean isCreatureWithinThisCell = (currentCell == getContainingCell());
////                CellMemory data = new CellMemory(VisualProcessor.getVisual(currentCell, this, viewableWalls.get(currentCell)),
////                        fData,(currentCell.canBeEntered(this) ? CellMemory.EnterableStatus.OPEN : CellMemory.EnterableStatus.CLOSED),
////                        currentCell.getX(), currentCell.getY(), isCreatureWithinThisCell, viewableWalls.get(currentCell));
////                currentFloorMemory.updateCell(currentCell.getX(), currentCell.getY(), data);
////            }
//
////            for (Entity entity : getFloor().getEntities()) {
////                if(entity != this && (viewableCells.contains(entity.getContainingCell()) && entity.getContainingCell().canBeSeenThrough(this) || true_sight_debug)) {
////
////
////                    List<Pair<Point, WorldUtils.Side>> intersectingTiles = WorldUtils.getIntersectedTilesWithWall(getWorldCenter().x, getWorldCenter().y, entity.getWorldCenter().x, entity.getWorldCenter().y);
////                    List<CellMemory> cellMemories = new ArrayList<>();
////                    for (Pair<Point, WorldUtils.Side> intersectingTile : intersectingTiles) {
////                        cellMemories.add(getCurrentFloorMemory().getDataAt(intersectingTile.getElement0()));
////                    }
////                    if(cellMemories.stream().noneMatch(cellMemory -> cellMemory == null || cellMemory.enterable == CellMemory.EnterableStatus.CLOSED) || true_sight_debug) {
////                        boolean chattable = (chatInteractive == entity);
////                        EntityStateData stateData = entity.getEntityStateData();
////                        List<RenderableObject> renderableObject = entity.getRawDrawables();
////                        EntityMemory em = new EntityMemory(stateData, renderableObject);
////                        currentFloorMemory.updateEntity(entity.getUUID(), em);
////                    }
////                }
////            }
//
//        }
    }

    public void cycleWieldedItem(){inventory.cycleWieldedItem();}

    public Item getWieldedItem(){return inventory.getWieldedItem();}

    public int getSightRange(){return sightRange;}

    public double getWalkSpeed() { return walkSpeed;}

    public Point2D getPoint2DLoc(){return new Point2D.Double(getWorldCenter().x, getWorldCenter().y);}

    public Inventory getInventory(){return inventory;}

    public abstract Brain getBrain();

    public abstract List<Sense<? extends Data, ? extends DataStreamParameters>> getSenses();

}
