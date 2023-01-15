package com.ewan.dunjeon.world.entities;

import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.items.Item;

import java.awt.*;
import java.util.function.Predicate;

public class ItemAsKinematicEntity extends KinematicEntity {

    final Item item;

    private final Predicate<Item> pickupPredicate;

    public ItemAsKinematicEntity(Item i) {
        this(i, item -> true);
    }

    public ItemAsKinematicEntity(Item i, Predicate<Item> pickupPredicate) {
        super(Color.PINK, i.getName(), 0.3f);
        this.item = i;
        this.pickupPredicate = pickupPredicate;
    }

    public Shape getRenderShape(){
        return item.getShape();
    }

    public boolean canBePickedUp(){
        return pickupPredicate.test(item);
    }

    @Override
    public void onCollideWithEntity(KinematicEntity e) {
        item.onEntityCollision(e);
    }

    @Override
    public void onCollideWithWall(BasicCell cell) {
        item.onWallCollision(cell);
    }


    public Item getItem(){return item;}
}
