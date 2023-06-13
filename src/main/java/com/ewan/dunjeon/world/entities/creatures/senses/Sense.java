package com.ewan.dunjeon.world.entities.creatures.senses;

import com.ewan.dunjeon.world.data.Data;
import com.ewan.dunjeon.world.data.DataStreamParameters;
import com.ewan.dunjeon.world.data.Datastream;
import com.ewan.dunjeon.world.entities.creatures.Creature;

public abstract class Sense<T extends Data, D extends DataStreamParameters> {
    protected Creature creature;
    protected Datastream<T, D> datastream;

    public Sense(Creature c, Datastream<T, D> d){
        creature = c;
        datastream = d;
    }

    public abstract void updateCreature(T data);
    public abstract D calculateParameters();
}
