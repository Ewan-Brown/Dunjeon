package com.ewan.dunjeon.world.item;

import com.ewan.dunjeon.world.World;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.Entity;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;

public abstract class Item{
    protected Item(String name){
        //Set all actions as not reasonable by default.
    }

    public String getName(){ return name;}
    public int getValue(){ return value;}
    public int getWeight(){ return weight;}

    private int value;
    private int weight;
    private String name;
//    public ItemType getType(){return type;}

    public void attemptDrink(Entity e){announceNothingHappened();};
    public void attemptConsume(Entity e){announceNothingHappened();};
    public void attemptMeleeAttack(Entity e, BasicCell target){announceNothingHappened();};
    public void attemptRangedAttack(Entity e, BasicCell target){announceNothingHappened();};
    public void attemptThrow(Entity e, BasicCell target){announceNothingHappened();};
    public void attemptCast(Entity e, BasicCell target){announceNothingHappened();};
    public void attemptPickup(Entity e){announceNothingHappened();};
    public void attemptDrop(Entity e){announceNothingHappened();};

    protected void announceNothingHappened(){
        System.out.println("Nothing Happened upon Item Usage. Are you using this correctly?");
    }

    /**
     * Should be triggered when an item hits another entity, in any way. Does everything that's NOT damage
     * @param e
     */
    public void onCollide(Entity e){};

}
