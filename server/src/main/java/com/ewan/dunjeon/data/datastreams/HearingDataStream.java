package com.ewan.dunjeon.data.datastreams;

import com.ewan.dunjeon.data.DataStreamParameters;
import com.ewan.dunjeon.data.Datastream;
import com.ewan.dunjeon.data.Sensor;
import com.ewan.dunjeon.data.SensorListener;
import com.ewan.dunjeon.server.world.Dunjeon;
import com.ewan.dunjeon.server.world.entities.creatures.Creature;
import com.ewan.meworking.data.server.event.GenericSoundEvent;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HearingDataStream extends Datastream<HearingDataStream.HearingDataStreamParameters> {

    private Set<GenericSoundEvent> amalgamatedSoundEvents = new HashSet<>();

    public void appendSoundEvent(GenericSoundEvent e){
        amalgamatedSoundEvents.add(e);
    }

    @Override
    public void update(Dunjeon d) {
        for (GenericSoundEvent amalgamatedSoundEvent : amalgamatedSoundEvents) {

        }
        amalgamatedSoundEvents.clear();
    }

    @Override
    public Sensor<HearingDataStreamParameters> constructSensorForDatastream(Creature c, Sensor.ParameterCalculator<HearingDataStreamParameters> pCalc) {
        return null;
    }

    public static class HearingDataStreamSensor extends Sensor<HearingDataStreamParameters>{

        public HearingDataStreamSensor(SensorListener l, Datastream<HearingDataStreamParameters> d, ParameterCalculator<HearingDataStreamParameters> pCalc) {
            super(l, d, pCalc);
        }
    }

    @AllArgsConstructor
    public static class HearingDataStreamParameters extends DataStreamParameters{

    }
}
