package com.ewan.dunjeon.data.datastreams;

import com.ewan.dunjeon.data.DataStreamParameters;
import com.ewan.dunjeon.data.Datastream;
import com.ewan.dunjeon.data.Sensor;
import com.ewan.dunjeon.data.SensorListener;
import com.ewan.dunjeon.server.world.Dunjeon;
import com.ewan.dunjeon.server.world.entities.creatures.Creature;
import lombok.AllArgsConstructor;

public class HearingDataStream extends Datastream<HearingDataStream.HearingDataStreamParameters> {

    @Override
    public void update(Dunjeon d) {

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
