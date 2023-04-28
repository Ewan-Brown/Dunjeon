package com.ewan.dunjeon.world.entities.memory.creaturedata;


import com.ewan.dunjeon.world.entities.memory.StateData;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Vector2;

/**
 * This is a class used to package generic data about an entity at a given moment. contains data for rendering and knowledge
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
