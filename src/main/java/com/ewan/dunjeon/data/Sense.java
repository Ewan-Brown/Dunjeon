package com.ewan.dunjeon.data;

import com.ewan.dunjeon.world.entities.creatures.Creature;

import java.util.List;

public class Sense<P extends DataStreamParameters> {
    protected Creature creature;
    protected Datastream<P> datastream;
    private ParameterCalculator<P> parameterCalculator;

    public Sense(Creature c, Datastream<P> d, ParameterCalculator<P> pCalc){
        creature = c;
        datastream = d;
        datastream.addSubscriber(this);
        parameterCalculator = pCalc;
    }

    public final void pollData(){
        List<Data> data = datastream.generateDataForParams(parameterCalculator.calculateParameter(creature));
        for (Data datum : data) {
            creature.getMemoryProcessor().processData(datum);
        }
    }

    public final void pollEvents(){
        List<Event> events = datastream.retrieveEventsForParams(parameterCalculator.calculateParameter(creature));
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

    public interface ParameterCalculator<P>{
        public P calculateParameter(Creature c);
    }
}
