package com.ewan.dunjeon.server.world.entities.creatures;

import com.ewan.dunjeon.data.DataStreamParameters;
import com.ewan.dunjeon.server.world.entities.Entity;
import com.ewan.dunjeon.data.Sensor;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;

import java.util.List;

public abstract class Creature extends Entity {
    public Creature(String name) {
        super(name);
    }

    @Override
    public void update(double stepSize) {

    }


    protected abstract List<Sensor<? extends DataStreamParameters>> getSensors();

    public void destroy(){
        getSensors().forEach(Sensor::destroy);
    }

    public abstract BasicMemoryBank getMemoryBank();

    public abstract CreatureControls<? extends Creature> getControls();

}
