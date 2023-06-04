package com.ewan.dunjeon.world.entities.memory.creaturedata;


import com.ewan.dunjeon.world.entities.memory.StateData;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Vector2;

/**
 * Contains data for physical representation
 */
public class PhysicalStateData extends StateData {

    Vector2 absolutePos;
    Vector2 velocity;
    long floorID;
    Polygon encasingPolygon;

    public PhysicalStateData(double time, long floor, Vector2 absPos, Vector2 vel, Polygon poly) {
        super(time);
        absolutePos = absPos;
        floorID = floor;
        velocity = vel;
        encasingPolygon = poly;
    }
}
