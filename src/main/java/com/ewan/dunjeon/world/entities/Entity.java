package com.ewan.dunjeon.world.entities;
import com.ewan.dunjeon.graphics.RenderableObject;
import com.ewan.dunjeon.world.CollidingObject;
import com.ewan.dunjeon.world.Pair;
import com.ewan.dunjeon.world.sounds.RelativeSoundEvent;
import com.ewan.dunjeon.world.level.Floor;
import com.ewan.dunjeon.world.cells.BasicCell;
import org.dyn4j.dynamics.Body;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Entity extends Body {

    private static long UUIDcounter = 0;

    private String name;

    private long UUID;

    public ZPositionState zState;

    public enum ZPositionState{
        CEILING,
        FLOATING,
        GROUND
    }

    private Floor floor;

    public String getName(){return name;}

    public Entity(String name){
        super();
        UUID = UUIDcounter;
        this.name = name;
        UUIDcounter++;
    }

    public void onEnterCell(BasicCell c){}

    public void update() {
        if(!exists()){
            System.out.println(exists() + " " + getName());
            throw new RuntimeException("Attempted to update dead entity");
        }
    }

    public boolean exists(){
        return true;
    }

    public void onSoundEvent(RelativeSoundEvent event){

    }

    public BasicCell getContainingCell() {
        return getFloor().getCellAt(getWorldCenter());
    }

    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }
    public long getUUID(){ return UUID;}

}
