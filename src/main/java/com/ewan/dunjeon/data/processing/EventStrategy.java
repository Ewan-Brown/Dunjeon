package com.ewan.dunjeon.data.processing;

import com.ewan.dunjeon.data.Event;

public interface EventStrategy<T extends Event>  {

    public void processEvent(T event);

}
