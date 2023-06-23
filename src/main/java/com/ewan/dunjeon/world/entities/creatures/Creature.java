package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.data.processing.EventProcessor;
import com.ewan.dunjeon.data.DataStreamParameters;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.data.Sensor;
import com.ewan.dunjeon.world.entities.BasicMemoryBank;
import com.ewan.dunjeon.world.items.inventory.HasInventory;
import com.ewan.dunjeon.world.items.inventory.Inventory;
import com.ewan.dunjeon.world.items.Item;
import com.ewan.dunjeon.world.items.inventory.InventoryWithWieldedItem;

import java.awt.geom.Point2D;
import java.util.List;

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

    private EventProcessor brain = new EventProcessor();
    private double loudStepChance = 0.001d; // Just for testing sound system - can be moved somewhere else :)

    @Override
    public void update(double stepSize) {
        super.update(stepSize);
    }

    protected void onPickupItem(Item i){ }


    public Point2D getPoint2DLoc(){return new Point2D.Double(getWorldCenter().x, getWorldCenter().y);}

    public Inventory getInventory(){return inventory;}

    public abstract BasicMemoryBank getMemoryProcessor();

    public abstract List<Sensor<? extends DataStreamParameters>> getSensors();

    public void destroy(){
        getSensors().forEach(Sensor::destroy);
    }
}
