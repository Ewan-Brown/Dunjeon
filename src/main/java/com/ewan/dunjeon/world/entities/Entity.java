package com.ewan.dunjeon.world.entities;
import com.ewan.dunjeon.world.Dunjeon;
import com.ewan.dunjeon.world.floor.Floor;
import org.dyn4j.dynamics.Body;

public abstract class Entity extends Body {

    private static long UUIDcounter = 0;
    private final String name;

    private long UUID;

    private Floor floor;

    public String getName(){return name;}

    public Entity(String name){
        super();
        UUID = UUIDcounter;
        this.name = name;
        UUIDcounter++;
    }

    private double cachedRotationAngle = 0;
    private int cachedRotationAngleTicks = 0;

    public double getRotationAngle(){
        if(cachedRotationAngleTicks != Dunjeon.getInstance().getTicksElapsed()){
            cachedRotationAngle = getTransform().getRotationAngle();
            cachedRotationAngleTicks = Dunjeon.getInstance().getTicksElapsed();
        }
        return cachedRotationAngle;
    }

    /**
     * Update anything realted to the physical aspect of this entity (NOT AI)
     * @param stepSize
     */
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
