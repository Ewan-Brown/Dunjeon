package com.ewan.dunjeon.server.world.entities.creatures;

import com.ewan.dunjeon.data.DataStreamParameters;
import com.ewan.dunjeon.data.SensorListener;
import com.ewan.dunjeon.server.world.entities.Entity;
import com.ewan.dunjeon.data.Sensor;
import com.ewan.meworking.data.server.data.Data;
import com.ewan.meworking.data.server.data.DataWrapper;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public abstract class Creature extends Entity implements SensorListener {
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

    @Override
    public void passOnData(DataWrapper<? extends Data, ?> data) {
        getMemoryBank().processWrappedData(data);
    }
}
