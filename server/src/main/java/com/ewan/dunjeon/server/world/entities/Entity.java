package com.ewan.dunjeon.server.world.entities;
import com.ewan.dunjeon.server.world.floor.Floor;
import com.ewan.dunjeon.server.world.Dunjeon;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.dynamics.Body;

public abstract class Entity extends Body {

    static Logger logger = LogManager.getLogger();
    private static long UUIDcounter = 0;
    private final String name;

    private final long UUID;

    @Getter
    @Setter
    private Floor floor;

    public String getName(){return name;}

    public Entity(String name){
        super();
        UUID = UUIDcounter;
        this.name = name;
        UUIDcounter++;
    }

    private double cachedRotationAngle = 0;
    private int cachedRotationAngleTick = -1;

    public final double getRotationAngle(){

        if(cachedRotationAngleTick != Dunjeon.getInstance().getTimestamp().serverTick()){
            cachedRotationAngle = getTransform().getRotationAngle();
        }
        return cachedRotationAngle;
    }

    /**
     * Update anything realted to the non-AI aspects of entity
     * @param stepSize
     */
    public abstract void update(double stepSize);

    public long getUUID(){ return UUID;}


}
