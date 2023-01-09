package com.ewan.dunjeon.world.entities;

import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.items.Item;

import java.awt.*;
import java.util.function.Predicate;

public class ItemAsEntity extends Entity{

    final Item item;

    private final Predicate<Item> pickupPredicate;

    public ItemAsEntity(Item i) {
        this(i, item -> true);
    }

    public ItemAsEntity(Item i, Predicate<Item> pickupPredicate) {
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
    public void onCollideWithEntity(Entity e) {
        item.onEntityCollision(e);
    }

    @Override
    public void onCollideWithWall(BasicCell cell) {
        item.onWallCollision(cell);
    }


    public Item getItem(){return item;}
}
