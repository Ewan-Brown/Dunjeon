package com.ewan.dunjeon.data.processing;

import com.ewan.dunjeon.data.Event;

import java.util.HashMap;


public class EventProcessor {

    private HashMap<Class<? extends Event>, EventStrategy<? extends Event>> eventStrategyMap = new HashMap<>();

    public <E extends Event> void processEvent(E e){
        if(eventStrategyMap.containsKey(e.getClass())) {
            @SuppressWarnings("unchecked")
            EventStrategy<E> strategy = (EventStrategy<E>) eventStrategyMap.get(e.getClass());
            strategy.processEvent(e);
        }else{
            throw new RuntimeException("Brain attempted to process event : " + e.getClass() + " but no corresponding strategy found");
        }
    }
}
