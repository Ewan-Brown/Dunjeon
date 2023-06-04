package com.ewan.dunjeon.world.entities;
import com.ewan.dunjeon.world.entities.memory.Brain;
import com.ewan.dunjeon.world.level.Floor;
import com.ewan.dunjeon.world.cells.BasicCell;
import org.dyn4j.dynamics.Body;

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

    /**
     * Returns the 'brain' associated with this entity. Could be one that is shared between mutiple entities
     * @return
     */

}
