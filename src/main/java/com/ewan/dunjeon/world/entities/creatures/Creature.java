package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.data.DataStreamParameters;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.data.Sensor;
import com.ewan.dunjeon.world.entities.BasicMemoryBank;
import com.ewan.dunjeon.world.entities.creaturecontroller.CreatureInterface;

import java.util.List;

public abstract class Creature extends Entity{
    public Creature(String name) {
        super(name);
    }

    @Override
    public void update(double stepSize) {
        super.update(stepSize);
    }

    public abstract BasicMemoryBank getMemoryProcessor();

    protected abstract List<Sensor<? extends DataStreamParameters>> getSensors();

    public abstract CreatureInterface getInterface();

    public void destroy(){
        getSensors().forEach(Sensor::destroy);
    }
}
