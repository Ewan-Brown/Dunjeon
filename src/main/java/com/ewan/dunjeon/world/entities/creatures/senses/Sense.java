package com.ewan.dunjeon.world.entities.creatures.senses;

import com.ewan.dunjeon.world.data.Data;
import com.ewan.dunjeon.world.data.DataStreamParameters;
import com.ewan.dunjeon.world.data.Datastream;
import com.ewan.dunjeon.world.entities.creatures.Creature;

public abstract class Sense<D extends Data, P extends DataStreamParameters> {
    protected Creature creature;
    protected Datastream<D, P> datastream;

    public Sense(Creature c, Datastream<D, P> d){
        creature = c;
        datastream = d;
    }

    public final void poll(){
        D data = datastream.generateDataForParams(calculateDatastreamParameters());
        creature.getBrain().processData(data);
    }

    public abstract P calculateDatastreamParameters();
}
