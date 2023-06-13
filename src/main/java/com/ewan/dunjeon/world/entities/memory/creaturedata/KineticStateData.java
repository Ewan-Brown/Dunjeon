package com.ewan.dunjeon.world.entities.memory.creaturedata;


import com.ewan.dunjeon.world.entities.memory.StateData;
import lombok.*;
import org.dyn4j.geometry.Vector2;

/**
 * Contains data for physical representation
 */
@Getter
@Setter
public class KineticStateData extends StateData {

    public KineticStateData(double time) {
        super(time);

    }

    Vector2 absolutePos;
    Vector2 velocity;

    Double rotation;
    Double rotationVelocity;
    Long floorID;
}
