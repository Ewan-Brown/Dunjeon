package com.ewan.dunjeon.data;

import com.ewan.dunjeon.world.entities.creatures.Creature;
import com.ewan.dunjeon.world.entities.memory.KnowledgeFragment;

import java.util.List;

public class Sensor<P extends DataStreamParameters> implements KnowledgeFragment.Source {
    final protected Creature creature;
    final protected Datastream<P> datastream;
    final private ParameterCalculator<P> parameterCalculator;

    protected Sensor(Creature c, Datastream<P> d, ParameterCalculator<P> pCalc){
        creature = c;
        datastream = d;
        datastream.addSubscriber(this);
        parameterCalculator = pCalc;
    }

    public final P getParameters(){return parameterCalculator.calculateParameter(creature);}

    public final void passOnData(List<? extends DataWrapper<? extends Data, ?>> data){
        for (DataWrapper<? extends Data, ?> datum : data) {
            creature.getMemoryBank().processWrappedData(datum);
        }
    }

    public final void passOnEvents(List<Event<DataWrapper<? extends Data, ?>>> events){
        for (Event<DataWrapper<? extends Data, ?>> event : events) {
            creature.getMemoryBank().processEventData(event);
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
