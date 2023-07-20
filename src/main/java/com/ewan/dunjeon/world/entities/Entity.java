package com.ewan.dunjeon.world.entities;
import com.ewan.dunjeon.world.level.Floor;
import com.ewan.dunjeon.world.cells.BasicCell;
import org.dyn4j.dynamics.Body;

public abstract class Entity extends Body {

    private static long UUIDcounter = 0;
    private String name;

    private long UUID;

    private Floor floor;

    public String getName(){return name;}

    public Entity(String name){
        super();
        UUID = UUIDcounter;
        this.name = name;
        UUIDcounter++;
    }


    public void update(double stepSize) {
    }

    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }
    public long getUUID(){ return UUID;}


}
