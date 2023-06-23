package com.ewan.dunjeon.data;

import com.ewan.dunjeon.world.entities.creatures.Creature;

import java.util.List;

public class Sensor<P extends DataStreamParameters> {
    protected Creature creature;
    protected Datastream<P> datastream;
    private ParameterCalculator<P> parameterCalculator;

    protected Sensor(Creature c, Datastream<P> d, ParameterCalculator<P> pCalc){
        creature = c;
        datastream = d;
        datastream.addSubscriber(this);
        parameterCalculator = pCalc;
    }

    public final P getParameters(){return parameterCalculator.calculateParameter(creature);}

    public final void passOnData(List<DataWrapper<? extends Data, ?>> data){
        for (DataWrapper<? extends Data, ?> datum : data) {
            creature.getMemoryProcessor().processWrappedData(datum);
        }
    }

    public final void passOnEvents(List<Event> events){
//        List<Event> events = datastream.re(parameterCalculator.calculateParameter(creature));
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
