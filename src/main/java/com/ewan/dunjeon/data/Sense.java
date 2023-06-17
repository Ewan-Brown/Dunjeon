package com.ewan.dunjeon.data;

import com.ewan.dunjeon.world.entities.creatures.Creature;

import java.util.List;

public abstract class Sense<D extends Data, P extends DataStreamParameters> {
    protected Creature creature;
    protected Datastream<D, P> datastream;

    public Sense(Creature c, Datastream<D, P> d){
        creature = c;
        datastream = d;
        datastream.addSubscriber(this);
    }

    public final void pollData(){
        D data = datastream.generateDataForParams(calculateDatastreamParameters());
        creature.getMemoryProcessor().processData(data);
    }

    public final void pollEvents(){
        List<Event> events = datastream.retrieveEventsForParams(calculateDatastreamParameters());
        for (Event event : events) {
            creature.getMemoryProcessor().processEvent(event);
        }
    }

    /**
     * Awkward but necessary to facilitate garbage collection with a two-way object link (Datastream<->Sense)
     */
    public final void destroy(){
        datastream.removeSubscriber(this);
    }

    public abstract P calculateDatastreamParameters();
}
