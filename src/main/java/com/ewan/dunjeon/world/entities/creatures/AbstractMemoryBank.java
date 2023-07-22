package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.data.Data;
import com.ewan.dunjeon.data.DataWrapper;
import com.ewan.dunjeon.data.Event;

public abstract class AbstractMemoryBank {

    public abstract void processWrappedData(DataWrapper<? extends Data, ?> wrappedData);
    public abstract void processEventData(Event e);
}
