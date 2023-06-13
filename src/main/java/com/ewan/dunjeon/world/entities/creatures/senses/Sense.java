package com.ewan.dunjeon.world.entities.creatures.senses;

import com.ewan.dunjeon.world.data.Data;
import com.ewan.dunjeon.world.data.DataStreamParameters;
import com.ewan.dunjeon.world.data.Datastream;
import com.ewan.dunjeon.world.entities.Entity;

public abstract class Sense<T extends Data, D extends DataStreamParameters> {
    protected Entity entity;

    public abstract void updateEntity(T data);
    public abstract D calculateParameters();
}
