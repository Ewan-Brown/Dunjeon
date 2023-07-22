package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.data.DataStreamParameters;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.data.Sensor;
import org.apache.commons.math3.analysis.function.Abs;

import java.util.List;

public abstract class Creature extends Entity{
    public Creature(String name) {
        super(name);
    }

    @Override
    public void update(double stepSize) {
        super.update(stepSize);
    }


    protected abstract List<Sensor<? extends DataStreamParameters>> getSensors();

    public void destroy(){
        getSensors().forEach(Sensor::destroy);
    }

    public abstract AbstractMemoryBank getMemoryProcessor();

}
