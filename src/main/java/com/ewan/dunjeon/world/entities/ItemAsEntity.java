package com.ewan.dunjeon.world.entities;

import com.ewan.dunjeon.world.items.Item;

import java.util.function.Predicate;

public class ItemAsEntity extends Entity {

    final Item item;

    private final Predicate<Item> pickupPredicate;

    public ItemAsEntity(Item i) {
        this(i, item -> true);
    }

    public ItemAsEntity(Item i, Predicate<Item> pickupPredicate) {
        super(i.getName());
        this.item = i;
        this.pickupPredicate = pickupPredicate;
    }

//    public Shape getRenderShape(){
//        return item.getShape();
//    }


//    @Override
//    public List<RenderableObject> getRawDrawables() {
//        return List.of(new RenderableObject(){
//
//            @Override
//            public Shape getShape() {
//                return item.getShape();
//            }
//
//            @Override
//            public Color getColor() {
//                return Color.BLUE;
//            }
//        });
//    }

    public boolean canBePickedUp(){
        return pickupPredicate.test(item);
    }

//    @Override
//    public void onCollideWithEntity(Entity e) {
//        item.onEntityCollision(e);
//    }
//
//    @Override
//    public void onCollideWithWall(BasicCell cell) {
//        item.onWallCollision(cell);
//    }

    public Item getItem(){return item;}
}
