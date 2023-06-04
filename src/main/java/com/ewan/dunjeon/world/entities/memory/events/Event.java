package com.ewan.dunjeon.world.entities.memory.events;

public class Event {
    private final double timestamp;
    public Event(double time){
        this.timestamp = time;
    }

    public double getTimestamp() {
        return timestamp;
    }

}
