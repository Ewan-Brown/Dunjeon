package com.ewan.dunjeon.world.entities.memory;


/**
Represents a snapshot of data about a particular thing at a given point in time.
 */
public abstract class StateData {
    private final double timestamp;
    public StateData(double time){
        this.timestamp = time;
    }

    public double getTimestamp() {
        return timestamp;
    }
}
