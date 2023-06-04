package com.ewan.dunjeon.world.entities.memory.events;

public interface EventStrategy<T extends Event>  {

    public void processEvent(T event);

}
