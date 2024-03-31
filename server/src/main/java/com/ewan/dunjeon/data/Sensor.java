package com.ewan.dunjeon.data;

import com.ewan.dunjeon.server.world.entities.creatures.Creature;
import com.ewan.meworking.data.server.data.Data;
import com.ewan.meworking.data.server.data.DataWrapper;
import com.ewan.meworking.data.server.memory.KnowledgeFragment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class Sensor<P extends DataStreamParameters> implements KnowledgeFragment.Source {
    //TODO This should probably just be wider/higher up the hierarchy, or even an interface!
    // Are we sure Creatures are the only things that can have sensors?
    final protected SensorListener sensorListener;
    final protected Datastream<P> datastream;
    final private ParameterCalculator<P> parameterCalculator;
    static Logger logger = LogManager.getLogger();

    protected Sensor(SensorListener l, Datastream<P> d, ParameterCalculator<P> pCalc){
        sensorListener = l;
        datastream = d;
        datastream.addSubscriber(this);
        parameterCalculator = pCalc;
    }

    public final P getParameters(){return parameterCalculator.calculateParameter(sensorListener);}

    public final void passOnData(List<? extends DataWrapper<? extends Data, ?>> data){
        for (DataWrapper<? extends Data, ?> datum : data) {
           sensorListener.passOnData(datum);
        }
    }

    /**
     * Awkward but necessary to facilitate garbage collection with a two-way object link (Datastream<->Sense)
     */
    public final void destroy(){
        datastream.removeSubscriber(this);
    }

    public interface ParameterCalculator<P>{
        public P calculateParameter(SensorListener c);
    }
}
