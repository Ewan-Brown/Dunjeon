package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.data.Data;
import com.ewan.dunjeon.data.DataWrapper;
import com.ewan.dunjeon.data.Event;
import com.ewan.dunjeon.world.entities.memory.KnowledgePackage;

public abstract class DataSink {

    public abstract <T extends Data, I, K extends KnowledgePackage<I,T>> void processWrappedData(DataWrapper<T, I> wrappedData);
    public abstract void processEventData(Event<?> e);
}
